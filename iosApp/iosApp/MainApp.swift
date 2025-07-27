import SwiftUI
import composeApp

@main
struct MainApp: App {
    init() {
        CoreMLMahjongDetectorCompanion.shared.impl = MahjongDetectorImpl()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
