package com.manage.source.service;

import com.manage.common.Constants;
import com.manage.source.bean.FileDetail;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
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
            if (file.isFile()) {
                String unit = "";
                fileDetail.setSize(getSize(file.length()));
                fileDetail.setUnit(getUnit(file.length()));
                String fileName = file.getName();
                fileDetail.setPic(getPic(fileName));
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

    /*创建文件夹*/
    public void makeDirs(String path) {
        File rootFile = new File(path);
        logger.info("make sure dirs exist " + rootFile.getAbsolutePath());
        if (!rootFile.exists() && rootFile.mkdirs()) {
            logger.info("创建目录成功：" + path);
        }
    }

    /*  根据文件名查询文件 */
    public List<FileDetail> getFilesByFileDir(List<FileDetail> fileList, String filePath,String filterName) throws Exception{

        File file = new File(filePath);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (null != files && files.length > 0) {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        this.getFilesByFileDir(fileList, file2.getAbsolutePath(),filterName);
                    } else if(file2.getName().toLowerCase().contains(filterName.toLowerCase())) {
                        FileDetail detail = new FileDetail();
                        String unit = new String() ;
                        detail.setName(file2.getName());
                        detail.setPath(file2.getPath());
                        detail.setSize(getSize(file2.length()));
                        detail.setUnit(getUnit(file2.length()));
                        detail.setType("file");
                        detail.setPic(getPic(file2.getName()));
                        String datetime = DateFormatUtils.format(file.lastModified(), Constants.DATE_FORMAT_YMDHM);
                        detail.setDate(datetime);
                        fileList.add(detail);
                    }
                }
            }
        } else {
            logger.info("文件不存在!");
        }
        return fileList;
    }
    /**/
    private String getPic(String fileName){
        String tmp = fileName.substring(fileName.lastIndexOf('.') + 1);
        String pic="";
        if(tmp != null){
            if ("gz".equals(tmp.toLowerCase()) || "tar".equals(tmp.toLowerCase()) || "zip".equals(tmp.toLowerCase())) {
                pic = "gz.png";
            } else if ("gif".equals(tmp.toLowerCase()) || "png".equals(tmp.toLowerCase()) || "jpg".equals(tmp.toLowerCase()) || "jpeg".equals(tmp.toLowerCase())) {
                pic = "img.png";
            } else if ("xls".equals(tmp.toLowerCase()) || "xlsx".equals(tmp.toLowerCase())) {
                pic = "excel.png";
            } else if ("pdf".equals(tmp.toLowerCase())) {
                pic = "excel.png";
            } else {
                pic = "other.png";
            }
        } else {
            pic = "other.png";
        }
        return pic;
    }

    private long getSize(long length){
        long size = 0l;
        if (length < 1024) {
            size = length;
        } else if (length < 1048576) {
            size = length / 1024;
        } else if (length < 1073741824) {
            size = length / 1048576;
        } else {
            size = length / 1073741824l;
        }
        return size;
    }
    private String getUnit(long length){
        if (length < 1024) {
            return "B";
        } else if (length < 1048576) {
            return "KB";
        } else if (length < 1073741824) {
            return "MB";
        } else {
            return "GB";
        }
    }
}
