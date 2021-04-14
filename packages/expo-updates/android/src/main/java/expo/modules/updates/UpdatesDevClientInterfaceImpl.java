package expo.modules.updates;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import androidx.annotation.Nullable;

import org.json.JSONObject;

import expo.modules.updates.db.DatabaseIntegrityCheck;
import expo.modules.updates.db.UpdatesDatabase;
import expo.modules.updates.db.entity.AssetEntity;
import expo.modules.updates.db.entity.UpdateEntity;
import expo.modules.updates.launcher.Launcher;
import expo.modules.updates.loader.RemoteLoader;
import expo.modules.updates.manifest.Manifest;
import expo.modules.updates.selectionpolicy.LauncherSelectionPolicySingleUpdate;
import expo.modules.updates.selectionpolicy.SelectionPolicy;

public class UpdatesDevClientInterfaceImpl implements UpdatesDevClientInterface {

  class UpdateImpl implements Update {
    private UUID mID;
    private Date mPublishedDate;
    private File mBundlePath;
    private JSONObject mManifest;

    public UpdateImpl(UUID id, Date publishedDate, File bundlePath, @Nullable JSONObject manifest) {
      mID = id;
      mPublishedDate = publishedDate;
      mBundlePath = bundlePath;
      mManifest = manifest;
    }

    public UUID getID() {
      return mID;
    }

    public Date getPublishedDate() {
      return mPublishedDate;
    }

    public File getBundlePath() {
      return mBundlePath;
    }

    public @Nullable JSONObject getManifest() {
      return mManifest;
    }
  }

  @Override
  public List<Update> getAvailableUpdates() {
    // TODO: should this get any embedded updates as well?
    UpdatesController controller = UpdatesController.getInstance();
    File updatesDirectory = controller.getUpdatesDirectory();

    UpdatesDatabase database = controller.getDatabase();
    DatabaseIntegrityCheck.run(database, updatesDirectory);
    List<UpdateEntity> launchableUpdates = database.updateDao().loadLaunchableUpdatesForScope(controller.getUpdatesConfiguration().getScopeKey());

    ArrayList<Update> availableUpdates = new ArrayList<>();
    for (UpdateEntity updateEntity : launchableUpdates) {
      AssetEntity launchAsset = database.updateDao().loadLaunchAsset(updateEntity.id);
      if (launchAsset == null || launchAsset.relativePath == null) {
        // this shouldn't happen since we just ran an integrity check, but just in case...
        continue;
      }
      UpdateImpl update = new UpdateImpl(
              updateEntity.id,
              updateEntity.commitTime,
              new File(updatesDirectory, launchAsset.relativePath),
              updateEntity.metadata);
      availableUpdates.add(update);
    }
    controller.releaseDatabase();

    return availableUpdates;
  }

  @Override
  public void fetchUpdateWithConfiguration(UpdatesConfiguration configuration, Context context, FetchCallback callback) {
    // TODO: what should this do if the update is already loaded?
    UpdatesController controller = UpdatesController.getInstance();
    UpdatesDatabase database = controller.getDatabase();
    RemoteLoader loader = new RemoteLoader(context, configuration, database, controller.getFileDownloader(), controller.getUpdatesDirectory());
    loader.start(new RemoteLoader.LoaderCallback() {
      @Override
      public void onFailure(Exception e) {
        callback.onFailure(e);
      }

      @Override
      public void onSuccess(@Nullable UpdateEntity update) {
        callback.onSuccess();
      }

      @Override
      public void onAssetLoaded(AssetEntity asset, int assetsLoaded, int totalAssets) {
        callback.onProgress(assetsLoaded, totalAssets);
      }

      @Override
      public boolean onManifestLoaded(Manifest manifest) {
        // TODO: should we actually check?
        return true;
      }
    });
  }

  @Override
  public void reload(Context context, ReloadCallback callback) {
    UpdatesController.getInstance().relaunchReactApplication(context, new Launcher.LauncherCallback() {
      @Override
      public void onFailure(Exception e) {
        callback.onFailure(e);
      }

      @Override
      public void onSuccess() {
        callback.onSuccess();
      }
    });
  }

  @Override
  public void setCurrentUpdate(Update update) {
    SelectionPolicy previousPolicy = UpdatesController.getInstance().getSelectionPolicy();
    UpdatesController.getInstance().setSelectionPolicy(new SelectionPolicy(
            new LauncherSelectionPolicySingleUpdate(update.getID()),
            previousPolicy.getLoaderSelectionPolicy(),
            previousPolicy.getReaperSelectionPolicy()));
  }
}
