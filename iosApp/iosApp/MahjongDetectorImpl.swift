//
//  MahjongDetectorImpl.swift
//  iosApp
//
//  Created by huangwenlong on 2025/7/26.
//  Copyright © 2025 orgName. All rights reserved.
//

import composeApp
import UIKit
import CoreML

class MahjongDetectorImpl : CoreMLMahjongDetector {
    private let model: best? = {
        do {
            let config = MLModelConfiguration()
            config.computeUnits = .all
            return try best(configuration: config)
        } catch {
            print("Error loading model: \(error)")
            return nil
        }
    }()
    
    func run(preprocessedImage: UIImage) async throws -> MLMultiArray {
        let input = try? bestInput(imageWith: preprocessedImage.cgImage!)
        let output = try? model?.prediction(input: input!)
        return (output?.featureValue(for: "var_1227")?.multiArrayValue)!
    }
    
}
