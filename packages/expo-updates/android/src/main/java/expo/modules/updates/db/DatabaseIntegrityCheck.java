package expo.modules.updates.db;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import expo.modules.updates.UpdatesConfiguration;
import expo.modules.updates.db.entity.AssetEntity;
import expo.modules.updates.db.entity.UpdateEntity;
import expo.modules.updates.loader.EmbeddedLoader;

public class DatabaseIntegrityCheck {
  public static void run(UpdatesDatabase database, File updatesDirectory, UpdatesConfiguration configuration, Context context) {
    List<AssetEntity> assets = database.assetDao().loadAllAssets();

    ArrayList<AssetEntity> missingAssets = new ArrayList<>();
    for (AssetEntity asset : assets) {
      if (asset.relativePath == null) {
        missingAssets.add(asset);
      } else {
        File path = new File(updatesDirectory, asset.relativePath);
        if (!path.exists()) {
          missingAssets.add(asset);
        }
      }
    }

    if (missingAssets.size() > 0) {
      database.assetDao().markMissingAssets(missingAssets);
    }

    ArrayList<UpdateEntity> updatesToDelete = new ArrayList<>();
    // we can't run any updates with the status EMBEDDED unless they match the current embedded update
    UpdateEntity embeddedUpdate = EmbeddedLoader.readEmbeddedManifest(context, configuration).getUpdateEntity();
    List<UpdateEntity> updatesWithEmbeddedStatus = database.updateDao().loadEmbeddedUpdates();
    for (UpdateEntity update : updatesWithEmbeddedStatus) {
      if (!update.id.equals(embeddedUpdate.id)) {
        updatesToDelete.add(update);
      }
    }

    if (updatesToDelete.size() > 0) {
      database.updateDao().deleteUpdates(updatesToDelete);
    }
  }
}
