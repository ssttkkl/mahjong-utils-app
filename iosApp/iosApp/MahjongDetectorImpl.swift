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
        let resizedImage = preprocessedImage.resized(to: CGSize(width: 640, height: 640))!
        let pixelBuffer = resizedImage.pixelBuffer()
        let output = try? model?.prediction(image: pixelBuffer!)
        let name = output?.featureNames.first
        let value = output?.featureValue(for: name!)
        return (value?.multiArrayValue)!
    }
    
}

// UIImage 扩展 - 图像处理
extension UIImage {
    func resized(to size: CGSize) -> UIImage? {
        UIGraphicsBeginImageContextWithOptions(size, false, self.scale)
        defer { UIGraphicsEndImageContext() }
        draw(in: CGRect(origin: .zero, size: size))
        return UIGraphicsGetImageFromCurrentImageContext()
    }
    
    func pixelBuffer() -> CVPixelBuffer? {
        let width = Int(size.width)
        let height = Int(size.height)
        
        var pixelBuffer: CVPixelBuffer?
        let status = CVPixelBufferCreate(
            kCFAllocatorDefault,
            width,
            height,
            kCVPixelFormatType_32BGRA,
            [
                kCVPixelBufferCGImageCompatibilityKey: kCFBooleanTrue,
                kCVPixelBufferCGBitmapContextCompatibilityKey: kCFBooleanTrue
            ] as CFDictionary,
            &pixelBuffer
        )
        
        guard status == kCVReturnSuccess, let buffer = pixelBuffer else {
            return nil
        }
        
        CVPixelBufferLockBaseAddress(buffer, [])
        defer { CVPixelBufferUnlockBaseAddress(buffer, []) }
        
        let context = CGContext(
            data: CVPixelBufferGetBaseAddress(buffer),
            width: width,
            height: height,
            bitsPerComponent: 8,
            bytesPerRow: CVPixelBufferGetBytesPerRow(buffer),
            space: CGColorSpaceCreateDeviceRGB(),
            bitmapInfo: CGImageAlphaInfo.noneSkipLast.rawValue
        )
        
        context?.translateBy(x: 0, y: CGFloat(height))
        context?.scaleBy(x: 1, y: -1)
        
        UIGraphicsPushContext(context!)
        draw(in: CGRect(x: 0, y: 0, width: width, height: height))
        UIGraphicsPopContext()
        
        return buffer
    }
}
