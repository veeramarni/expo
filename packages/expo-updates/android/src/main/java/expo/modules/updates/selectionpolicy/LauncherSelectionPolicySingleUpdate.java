package expo.modules.updates.selectionpolicy;

import org.json.JSONObject;

import java.util.List;

import expo.modules.updates.db.entity.UpdateEntity;

/**
 * Trivial LauncherSelectionPolicy that will choose a single predetermined update to launch.
 */
public class LauncherSelectionPolicySingleUpdate implements LauncherSelectionPolicy {

  private UpdateEntity mUpdate;

  public LauncherSelectionPolicySingleUpdate(UpdateEntity update) {
    mUpdate = update;
  }

  @Override
  public UpdateEntity selectUpdateToLaunch(List<UpdateEntity> updates, JSONObject filters) {
    for (UpdateEntity update : updates) {
      if (update.id.equals(mUpdate.id)) {
        return update;
      }
    }
    return null;
  }
}
