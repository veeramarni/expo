package expo.modules.updates.db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import expo.modules.updates.db.entity.AssetEntity;

public class DatabaseIntegrityCheck {
  public static void run(UpdatesDatabase database, File updatesDirectory) {
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
  }
}
