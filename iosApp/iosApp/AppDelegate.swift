import Foundation
import UIKit
import composeApp

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        let composeController = MainViewControllerKt.MainViewController()
        
        window = UIWindow(frame: UIScreen.main.bounds)
        window?.rootViewController = composeController
        window?.makeKeyAndVisible()

        return true
    }

}
