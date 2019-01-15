package com.manage.source.service;

import com.manage.common.Constants;
import com.manage.source.bean.FileDetail;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by luya on 2018/6/16.
 */
@Service
public class FileService {
    private Log logger = LogFactory.getLog(FileService.class);

    /*过滤目标地址下的文件列表*/
    public List<FileDetail> listAdminFiles(File currentFile) {
        List<FileDetail> results = new LinkedList<>();
        File[] files = currentFile.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }
        for (File file : files) {
            FileDetail fileDetail = new FileDetail();
            fileDetail.setName(file.getName());
            fileDetail.setSize(file.length());
            fileDetail.setPath(file.getPath());
            String datetime = DateFormatUtils.format(file.lastModified(), Constants.DATE_FORMAT_YMDHM);
            fileDetail.setDate(datetime);
            /*fileDetail.setDate(new SimpleDateFormat("yyyy/MM/dd HH:mm")
                    .format(new Date(file.lastModified())));*/
            if (file.isFile()) {
                long size = file.length() / 1024;
                fileDetail.setUnit("KB");
                if (size > 1000) {
                    size = size / 1024;
                    fileDetail.setUnit("MB");
                }
                size = size == 0 ? 1 : size;
                fileDetail.setSize(size);// MB
                String fileName = file.getName();
                String pic = "";
                String tmp = fileName.substring(fileName.lastIndexOf('.') + 1);
                if ("gz".equals(tmp) || "tar".equals(tmp) || "zip".equals(tmp)) {
                    pic = "gz.png";
                } else if ("gif".equals(tmp) || "png".equals(tmp) || "jpg".equals(tmp) || "jpeg".equals(tmp)) {
                    pic = "img.png";
                } else if ("xls".equals(tmp) || "xlsx".equals(tmp)) {
                    pic = "excel.png";
                } else {
                    pic = "other.png";
                }
                fileDetail.setPic(pic);
                fileDetail.setType("file");
            } else {
                fileDetail.setType("path");
                fileDetail.setPic("file.png");
            }
            results.add(fileDetail);
        }
        return results;
    }

    /*上传文件*/
    public void uploadFile(HttpServletRequest request, String path, String fileName) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        File fileTmp = null;
        try {
            File savePath = new File(path);
            if (!savePath.exists() && savePath.mkdirs()) {
                logger.info("修改目录名称成功！");
            }
            File file = new File(path + File.separator + fileName);

            if (file.exists()) {
                StringBuffer name = new StringBuffer();
                String date = "_" + new SimpleDateFormat(Constants.DATE_FORMAT_YMDHMS).format(new Date());
                name.append(fileName).insert(fileName.lastIndexOf("."), date);
                file = new File(path + File.separator + name.toString());
            }
            fileTmp = new File(path + File.separator + fileName + ".tmp");

            inputStream = request.getInputStream();
            outputStream = new FileOutputStream(fileTmp);
            int byteCount = 0;
            byte[] bytes = new byte[1024 * 1024];
            while ((byteCount = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, byteCount);
            }
            inputStream.close();
            outputStream.close();
            if (fileTmp.renameTo(file)) {
                logger.info("修改文件名称成功");
            }

        } catch (IOException ioe) {
            logger.error(ioe);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    logger.error(ex);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    logger.error(ex);
                }
            }
            if (fileTmp != null && fileTmp.delete()) {
                logger.info("删除成功！");
            }
        }
    }

    /*删除文件或文件夹*/
    public boolean deleteFile(String path) {
        if (StringUtils.isEmpty(path)) {
            return true;
        }
        boolean result = false;
        try {
            File file = new File(path);
            if (file.isDirectory()) {
                FileUtils.deleteDirectory(file);
                result = true;
            } else {
                file.delete();
                result = true;
            }
        } catch (Exception e) {
            result = false;
        }
        return result;
    }
    /*下载文件*/


    /*创建文件夹*/
    public void makeDirs(String path) {
        File rootFile = new File(path);
        logger.info("make sure dirs exist " + rootFile.getAbsolutePath());
        if (!rootFile.exists() && rootFile.mkdirs()) {
            logger.info("创建目录成功：" + path);
        }
    }
}
