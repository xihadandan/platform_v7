package com.wellsoft.pt.repository.web;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileSignatures {

    private static final Map<String, List<int[]>> FILE_SIGNATURES = new HashMap<>();

    static {
        // PNG
        FILE_SIGNATURES.put("png", Arrays.asList(
                new int[]{0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A} // PNG
        ));

        // JPEG/JPG
        FILE_SIGNATURES.put("jpg", Arrays.asList(
                new int[]{0xFF, 0xD8, 0xFF, 0xE0}, // JPEG
                new int[]{0xFF, 0xD8, 0xFF, 0xE1}, // JPEG
                new int[]{0xFF, 0xD8, 0xFF, 0xE2}  // JPEG
        ));
        FILE_SIGNATURES.put("jpeg", FILE_SIGNATURES.get("jpg")); // 复用 JPEG 的签名

        // JFIF
        FILE_SIGNATURES.put("jfif", Arrays.asList(
                new int[]{0xFF, 0xD8, 0xFF, 0xE0} // JFIF
        ));

        // HEIC
        FILE_SIGNATURES.put("heic", Arrays.asList(
                new int[]{0x00, 0x00, 0x00, 0x20, 0x66, 0x74, 0x79, 0x70, 0x68, 0x65, 0x69, 0x63} // HEIC
        ));

        // GIF
        FILE_SIGNATURES.put("gif", Arrays.asList(
                new int[]{0x47, 0x49, 0x46, 0x38} // GIF
        ));

        // BMP/DIB
        FILE_SIGNATURES.put("bmp", Arrays.asList(
                new int[]{0x42, 0x4D} // BMP
        ));
        FILE_SIGNATURES.put("dib", FILE_SIGNATURES.get("bmp")); // 复用 BMP 的签名

        // TIFF/TIF
        FILE_SIGNATURES.put("tiff", Arrays.asList(
                new int[]{0x49, 0x49, 0x2A, 0x00}, // TIFF (little-endian)
                new int[]{0x4D, 0x4D, 0x00, 0x2A}  // TIFF (big-endian)
        ));
        FILE_SIGNATURES.put("tif", FILE_SIGNATURES.get("tiff")); // 复用 TIFF 的签名

        // WEBP
        FILE_SIGNATURES.put("webp", Arrays.asList(
                new int[]{0x52, 0x49, 0x46, 0x46} // WEBP
        ));

        // PDF
        FILE_SIGNATURES.put("pdf", Arrays.asList(
                new int[]{0x25, 0x50, 0x44, 0x46} // PDF
        ));

        // ZIP
        FILE_SIGNATURES.put("zip", Arrays.asList(
                new int[]{0x50, 0x4B, 0x03, 0x04} // ZIP
        ));

        // RAR
        FILE_SIGNATURES.put("rar", Arrays.asList(
                new int[]{0x52, 0x61, 0x72, 0x21, 0x1A, 0x07, 0x00} // RAR
        ));

        // 7-Zip
        FILE_SIGNATURES.put("7z", Arrays.asList(
                new int[]{0x37, 0x7A, 0xBC, 0xAF, 0x27, 0x1C} // 7-Zip
        ));

        // GZIP
        FILE_SIGNATURES.put("gzip", Arrays.asList(
                new int[]{0x1F, 0x8B} // GZIP
        ));

        // MP3
        FILE_SIGNATURES.put("mp3", Arrays.asList(
                new int[]{0x49, 0x44, 0x33}, // MP3 (ID3v2)
                new int[]{0xFF, 0xFB}        // MP3 (no ID3)
        ));

        // WAV
        FILE_SIGNATURES.put("wav", Arrays.asList(
                new int[]{0x52, 0x49, 0x46, 0x46} // WAV
        ));

        // FLAC
        FILE_SIGNATURES.put("flac", Arrays.asList(
                new int[]{0x66, 0x4C, 0x61, 0x43} // FLAC
        ));

        // MP4
        FILE_SIGNATURES.put("mp4", Arrays.asList(
                new int[]{0x00, 0x00, 0x00, 0x18, 0x66, 0x74, 0x79, 0x70, 0x6D, 0x70, 0x34, 0x32} // MP4
        ));

        // AVI
        FILE_SIGNATURES.put("avi", Arrays.asList(
                new int[]{0x52, 0x49, 0x46, 0x46} // AVI
        ));

        // MKV
        FILE_SIGNATURES.put("mkv", Arrays.asList(
                new int[]{0x1A, 0x45, 0xDF, 0xA3} // MKV
        ));

        // EXE/DLL
        FILE_SIGNATURES.put("exe", Arrays.asList(
                new int[]{0x4D, 0x5A} // EXE
        ));
        FILE_SIGNATURES.put("dll", FILE_SIGNATURES.get("exe")); // 复用 EXE 的签名

        // DOC/XLS/PPT (Office 97-2003)
        FILE_SIGNATURES.put("doc", Arrays.asList(
                new int[]{0xD0, 0xCF, 0x11, 0xE0, 0xA1, 0xB1, 0x1A, 0xE1} // DOC
        ));
        FILE_SIGNATURES.put("xls", FILE_SIGNATURES.get("doc")); // 复用 DOC 的签名
        FILE_SIGNATURES.put("ppt", FILE_SIGNATURES.get("doc")); // 复用 DOC 的签名

        // DOCX/XLSX/PPTX (Office 2007+)
        FILE_SIGNATURES.put("docx", Arrays.asList(
                new int[]{0x50, 0x4B, 0x03, 0x04} // DOCX
        ));
        FILE_SIGNATURES.put("xlsx", FILE_SIGNATURES.get("docx")); // 复用 DOCX 的签名
        FILE_SIGNATURES.put("pptx", FILE_SIGNATURES.get("docx")); // 复用 DOCX 的签名

        // WPS/ET/DPS (WPS Office)
        FILE_SIGNATURES.put("wps", Arrays.asList(
                new int[]{0xD0, 0xCF, 0x11, 0xE0, 0xA1, 0xB1, 0x1A, 0xE1} // WPS
        ));
        FILE_SIGNATURES.put("et", FILE_SIGNATURES.get("wps")); // 复用 WPS 的签名
        FILE_SIGNATURES.put("dps", FILE_SIGNATURES.get("wps")); // 复用 WPS 的签名

        // DMG
        FILE_SIGNATURES.put("dmg", Arrays.asList(
                new int[]{0x78, 0x01, 0x73, 0x0D, 0x62, 0x62, 0x60} // DMG
        ));
    }

    /**
     * 获取文件签名表
     */
    public static Map<String, List<int[]>> getFileSignatures() {
        return FILE_SIGNATURES;
    }


}