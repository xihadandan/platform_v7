package com.wellsoft.pt.repository.support.convert;

public interface PDFConverter {
    public void convert2PDF(String inputFile, String pdfFile);

    public void convert2PDF(String inputFile);

    public void convert2PDF(String[] inputFiles, String[] pdfFiles);

    public void convert2PDF(String[] inputFiles);
}
