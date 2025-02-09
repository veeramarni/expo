"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.withDevLauncherAppDelegate = exports.modifyAppDelegate = void 0;
const config_plugins_1 = require("@expo/config-plugins");
const DEV_LAUNCHER_APP_DELEGATE_SOURCE_FOR_URL = `  #if defined(EX_DEV_LAUNCHER_ENABLED)
  return [[EXDevLauncherController sharedInstance] sourceUrl];
  #else
  return [[RCTBundleURLProvider sharedSettings] jsBundleURLForBundleRoot:@"index" fallbackResource:nil];
  #endif`;
const DEV_LAUNCHER_APP_DELEGATE_ON_DEEP_LINK = `#if defined(EX_DEV_LAUNCHER_ENABLED)
  if ([EXDevLauncherController.sharedInstance onDeepLink:url options:options]) {
    return true;
  }
  #endif
  return [RCTLinkingManager application:application openURL:url options:options];`;
const DEV_LAUNCHER_APP_DELEGATE_IOS_IMPORT = `
#if defined(EX_DEV_LAUNCHER_ENABLED)
#include <EXDevLauncher/EXDevLauncherController.h>
#endif`;
const DEV_LAUNCHER_APP_DELEGATE_CONTROLLER_DELEGATE = `
#if defined(EX_DEV_LAUNCHER_ENABLED)
@implementation AppDelegate (EXDevLauncherControllerDelegate)

- (void)devLauncherController:(EXDevLauncherController *)developmentClientController
    didStartWithSuccess:(BOOL)success
{
  developmentClientController.appBridge = [self initializeReactNativeApp];
  EXSplashScreenService *splashScreenService = (EXSplashScreenService *)[UMModuleRegistryProvider getSingletonModuleForClass:[EXSplashScreenService class]];
  [splashScreenService showSplashScreenFor:self.window.rootViewController];
}

@end
#endif
`;
const DEV_LAUNCHER_APP_DELEGATE_INIT = `#if defined(EX_DEV_LAUNCHER_ENABLED)
        EXDevLauncherController *controller = [EXDevLauncherController sharedInstance];
        [controller startWithWindow:self.window delegate:(id<EXDevLauncherControllerDelegate>)self launchOptions:launchOptions];
      #else
        [self initializeReactNativeApp];
      #endif`;
const DEV_LAUNCHER_APP_DELEGATE_BRIDGE = `#if defined(EX_DEV_LAUNCHER_ENABLED)
    NSDictionary *launchOptions = [EXDevLauncherController.sharedInstance getLaunchOptions];
  #else
    NSDictionary *launchOptions = self.launchOptions;
  #endif
  
    RCTBridge *bridge = [[RCTBridge alloc] initWithDelegate:self launchOptions:launchOptions];`;
function modifyAppDelegate(appDelegate) {
    if (!appDelegate.includes(DEV_LAUNCHER_APP_DELEGATE_IOS_IMPORT)) {
        const lines = appDelegate.split('\n');
        lines.splice(1, 0, DEV_LAUNCHER_APP_DELEGATE_IOS_IMPORT);
        appDelegate = lines.join('\n');
    }
    if (!appDelegate.includes(DEV_LAUNCHER_APP_DELEGATE_INIT)) {
        appDelegate = appDelegate.replace(/(didFinishLaunchingWithOptions([^}])*)\[self initializeReactNativeApp\];(([^}])*})/, `$1${DEV_LAUNCHER_APP_DELEGATE_INIT}$3`);
    }
    if (!appDelegate.includes(DEV_LAUNCHER_APP_DELEGATE_BRIDGE)) {
        appDelegate = appDelegate.replace('RCTBridge *bridge = [[RCTBridge alloc] initWithDelegate:self launchOptions:self.launchOptions];', DEV_LAUNCHER_APP_DELEGATE_BRIDGE);
    }
    if (!appDelegate.includes(DEV_LAUNCHER_APP_DELEGATE_SOURCE_FOR_URL)) {
        appDelegate = appDelegate.replace('return [[RCTBundleURLProvider sharedSettings] jsBundleURLForBundleRoot:@"index" fallbackResource:nil];', DEV_LAUNCHER_APP_DELEGATE_SOURCE_FOR_URL);
    }
    if (!appDelegate.includes(DEV_LAUNCHER_APP_DELEGATE_ON_DEEP_LINK)) {
        appDelegate = appDelegate.replace('return [RCTLinkingManager application:application openURL:url options:options];', DEV_LAUNCHER_APP_DELEGATE_ON_DEEP_LINK);
    }
    if (!appDelegate.includes(DEV_LAUNCHER_APP_DELEGATE_CONTROLLER_DELEGATE)) {
        appDelegate += DEV_LAUNCHER_APP_DELEGATE_CONTROLLER_DELEGATE;
    }
    return appDelegate;
}
exports.modifyAppDelegate = modifyAppDelegate;
exports.withDevLauncherAppDelegate = config => {
    return config_plugins_1.withAppDelegate(config, config => {
        if (config.modResults.language === 'objc') {
            config.modResults.contents = modifyAppDelegate(config.modResults.contents);
        }
        else {
            config_plugins_1.WarningAggregator.addWarningIOS('expo-dev-launcher', 'Swift AppDelegate files are not supported yet.');
        }
        return config;
    });
};
