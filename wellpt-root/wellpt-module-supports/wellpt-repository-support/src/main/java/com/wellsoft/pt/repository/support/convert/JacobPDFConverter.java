package com.wellsoft.pt.repository.support.convert;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComFailException;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class JacobPDFConverter implements PDFConverter {
    private static final Logger LOG = LoggerFactory.getLogger(JacobPDFConverter.class);
    private static final int wdFormatPDF = 17;
    private static final int xlTypePDF = 0;
    private static final int ppSaveAsPDF = 32;

    public static void word2PDF(String inputFile, String pdfFile) {
        ActiveXComponent app = null;
        Dispatch doc = null;
        try {
            ComThread.InitSTA();
            app = new ActiveXComponent("Word.Application"); //��wordӦ�ó���
            app.setProperty("Visible", false); //����word���ɼ�
            Dispatch docs = app.getProperty("Documents").toDispatch(); //���word�����д򿪵��ĵ�,����Documents����
            //����Documents������Open�������ĵ��������ش򿪵��ĵ�����Document
            doc = Dispatch.call(docs, "Open", inputFile, false, true).toDispatch();
            Dispatch.call(doc, "ExportAsFixedFormat", pdfFile, wdFormatPDF //word����Ϊpdf��ʽ�ֵ꣬Ϊ17
            );
        } catch (ComFailException e) {
            LOG.error(e.getMessage(), e);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (doc != null) {
                Dispatch.call(doc, "Close", false); //�ر��ĵ�
            }
            if (app != null) {
                app.invoke("Quit", 0); //�ر�wordӦ�ó���
            }
            ComThread.Release();
        }
    }

    public static void excel2PDF(String inputFile, String pdfFile) {
        ActiveXComponent app = null;
        Dispatch excel = null;
        try {
            ComThread.InitSTA();
            app = new ActiveXComponent("Excel.Application");
            app.setProperty("Visible", false);
            Dispatch excels = app.getProperty("Workbooks").toDispatch();
            excel = Dispatch.call(excels, "Open", inputFile, false, true).toDispatch();
            Dispatch.call(excel, "ExportAsFixedFormat", xlTypePDF, pdfFile);
        } catch (ComFailException e) {
            LOG.error(e.getMessage(), e);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (excel != null) {
                Dispatch.call(excel, "Close", false);
            }
            if (app != null) {
                app.invoke("Quit");
            }
            ComThread.Release();
        }
    }

    public static void ppt2PDF(String inputFile, String pdfFile) {
        ActiveXComponent app = null;
        Dispatch ppt = null;
        try {
            ComThread.InitSTA();
            app = new ActiveXComponent("PowerPoint.Application");
            //app.setProperty("Visible", false);
            Dispatch ppts = app.getProperty("Presentations").toDispatch();
            ppt = Dispatch.call(ppts, "Open", inputFile, true, //ReadOnly
                    true, //Untitledָ���ļ��Ƿ��б���
                    false//WithWindowָ���ļ��Ƿ�ɼ�
            ).toDispatch();
            Dispatch.call(ppt, "SaveAs", pdfFile, ppSaveAsPDF);
        } catch (ComFailException e) {
            LOG.error(e.getMessage(), e);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (ppt != null) {
                Dispatch.call(ppt, "Close");
            }
            if (app != null) {
                app.invoke("Quit");
            }
            ComThread.Release();
        }
    }

    public static int getPercent(float h, float w) {
        int p = 0;
        float p2 = 0.0f;
        p2 = 530 / w * 100;
        p = Math.round(p2);
        return p;
    }

    public void convert2PDF(String inputFile, String pdfFile) {
        String suffix = FileUtil.getFileSufix(inputFile);
        File file = new File(inputFile);
        if (!file.exists()) {
            System.out.println(inputFile + " does not exist");
            return;
        }
        if (suffix.equals("pdf")) {
            System.out.println("PDF do not need to convert!");
            return;
        }
        boolean isImg = false;//FileUtil.isImage(inputFile);
        try {
            isImg = FileUtil.isImage(inputFile);
        } catch (Exception ce) {
            LOG.error(ce.getMessage(), ce);
        }
        if (isImg) {
            img2PDF(inputFile, pdfFile);
        } else if (suffix.equals("doc") || suffix.equals("docx") || suffix.equals("txt")) {
            word2PDF(inputFile, pdfFile);
        } else if (suffix.equals("ppt") || suffix.equals("pptx")) {
            ppt2PDF(inputFile, pdfFile);
        } else if (suffix.equals("xls") || suffix.equals("xlsx")) {
            excel2PDF(inputFile, pdfFile);
        } else if (suffix.equals("wps")) {
            //wps2PDF(inputFile,pdfFile);
            word2PDF(inputFile, pdfFile);
        } else {
            //System.out.println("�ļ���ʽ��֧��ת��!");
            word2PDF(inputFile, pdfFile);
        }
    }

    public void convert2PDF(String inputFile) {
        String pdfFile = FileUtil.getFilePrefix(inputFile) + ".pdf";
        convert2PDF(inputFile, pdfFile);

    }

    public void convert2PDF(String[] inputFiles, String[] pdfFiles) {
        try {
            for (int i = 0; i < inputFiles.length; i++) {
                String inputFile = inputFiles[i];
                String pdfFile = pdfFiles[i];
                if (inputFile == null || inputFile.equals(""))
                    continue;
                convert2PDF(inputFile, pdfFile);
            }
        } catch (Exception ce) {
            LOG.error(ce.getMessage(), ce);
        }
    }

    public void convert2PDF(String[] inputFiles) {
        String pdfFiles[] = new String[inputFiles.length];
        for (int i = 0; i < inputFiles.length; i++) {
            String inputFile = inputFiles[i];
            String pdfFile = FileUtil.getFilePrefix(inputFile) + ".pdf";
            pdfFiles[i] = pdfFile;
        }
        convert2PDF(inputFiles, pdfFiles);
    }

    public void wps2PDF(String inputFile, String pdfFile) {
        File sFile = new File(inputFile);
        File tFile = new File(pdfFile);
        ActiveXComponent wps = null;
        try {
            ComThread.InitSTA();
            wps = new ActiveXComponent("wps.application");
            ActiveXComponent doc = wps.invokeGetComponent("Documents").invokeGetComponent("Open",
                    new Variant(sFile.getAbsolutePath()));
            doc.invoke("ExportPdf", new Variant(tFile.getAbsolutePath()));
            doc.invoke("Close");
            doc.safeRelease();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (wps != null) {
                wps.invoke("Terminate");
                wps.safeRelease();
            }
            ComThread.Release();
        }
    }

    public void img2PDF(String inputFile, String pdfFile) {
        Document doc = new Document(PageSize.A4, 20, 20, 20, 20);
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(pdfFile));
            doc.open();
            doc.newPage();
            Image img = Image.getInstance(inputFile);
            float heigth = img.getHeight();
            float width = img.getWidth();
            int percent = getPercent(heigth, width);
            img.setAlignment(Image.MIDDLE);
            img.scalePercent(percent + 3);// ��ʾ��ԭ��ͼ��ı���;
            doc.add(img);
            doc.close();
        } catch (FileNotFoundException e) {
            LOG.error(e.getMessage(), e);
        } catch (DocumentException e) {
            LOG.error(e.getMessage(), e);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        File mOutputPdfFile = new File(pdfFile);
        if (!mOutputPdfFile.exists()) {
            mOutputPdfFile.deleteOnExit();
            return;
        }
    }
}
