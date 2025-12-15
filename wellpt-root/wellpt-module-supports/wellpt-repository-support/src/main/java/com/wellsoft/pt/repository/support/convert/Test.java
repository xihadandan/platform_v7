package com.wellsoft.pt.repository.support.convert;


public class Test {

    /**
     * 如何描述该方法
     *
     * @param args
     */
    public static void main(String[] args) {
        PDFConverter pdfConverter = new JacobPDFConverter();
        pdfConverter.convert2PDF("c:\\1.docx");
    }

}
