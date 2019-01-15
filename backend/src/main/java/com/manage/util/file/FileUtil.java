package com.manage.util.file;

import com.manage.exception.DocException;
import com.manage.exception.impl.BizExceptionStatusEnum;
import com.manage.source.bean.FileDetail;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 获取外部资源文件，如图片列表，Docker镜像等
 */
@RestController
@ConfigurationProperties(prefix = "file")
public class FileUtil {
    private Log logger = LogFactory.getLog(FileUtil.class);
    private String fileRootPath;
    private String uploadedDockerImagesPath;
    private String uploadedGZFilePath;
    private String externalRes;
    private String uploadedPicturePath;
    private String prex = "file:";

    public void setUploadedFilePath(String uploadedPicturePath) {
        this.uploadedPicturePath = uploadedPicturePath;
    }

    public void setExternalRes(String externalRes) {
        if (!externalRes.startsWith(prex)) {
            //TODO
            throw new DocException(BizExceptionStatusEnum.ROLE_SELETE_ERROR);
        }
        this.externalRes = externalRes.substring(prex.length());
        makeSureDirs(this.externalRes);
    }

    public void setUploadedDockerImagesPath(String uploadedImagesPath) {
        this.uploadedDockerImagesPath = uploadedImagesPath;

    }

    public void setUploadedGZFilePath(String uploadedGZFilePath) {
        this.uploadedGZFilePath = uploadedGZFilePath;

    }

    public void makeSureDirs(String dir) {
        File rootFile = new File(dir);
        logger.info("make sure dirs exist " + rootFile.getAbsolutePath());
        if (!rootFile.exists() && rootFile.mkdirs()) {
            logger.info("创建成功！");
        }
    }

    private List<File> filerFiles(File dir, String[] extNames) {
        List<File> results = new LinkedList<>();

        File[] files = dir.listFiles();

        if (files == null) {
            return Collections.emptyList();
        }
        for (File file : files) {
            String fileName = file.getName();
            if (file.isFile()) {
                boolean matched = false;
                for (String ext : extNames) {
                    if (fileName != null && fileName.endsWith(ext)) {
                        matched = true;
                        break;
                    }
                }
                if (matched) {
                    results.add(file);
                }
            }
        }
        return results;
    }

    /**
     * @return map of file name and size
     */
    @RequestMapping(value = "/extresources/listuploadedimages")
    public Map<String, Integer> getUploadedImagesList() {
        String[] extNames = {".img", ".tar", ".tar.gz"};
        Map<String, Integer> resultFiles = new LinkedHashMap<>();
        File rootFile = new File(externalRes, uploadedDockerImagesPath);
        List<File> matchedFiles = filerFiles(rootFile, extNames);
        for (File file : matchedFiles) {
            String fileName = file.getName();
            int size = ((int) (file.length() / 1024 / 1024));
            size = size == 0 ? 1 : size;
            resultFiles.put(fileName, size);
        }
        return resultFiles;
    }

    /**
     * @return map of file name and size
     */
    @RequestMapping(value = "/extresources/listuploadedGzs")
    public Map<String, Integer> getUploadedGzList() {
        String[] extNames = {".tar", ".tar.gz"};
        Map<String, Integer> resultFiles = new LinkedHashMap<>();
        File rootFile = new File(externalRes, uploadedGZFilePath);
        List<File> matchedFiles = filerFiles(rootFile, extNames);
        for (File file : matchedFiles) {
            String fileName = file.getName();
            resultFiles.put(fileName, (int) (file.length() / 1024 / 1024));
        }
        return resultFiles;
    }

    private List<FileDetail> listFiles(File dir) {
        List<FileDetail> results = new LinkedList<>();
        File[] files = dir.listFiles();

        if (files == null) {
            return Collections.emptyList();
        }
        for (File file : files) {
            FileDetail fileDetail = new FileDetail();
            fileDetail.setName(file.getName());
            fileDetail.setSize(file.length());
            fileDetail.setDate(new SimpleDateFormat("yyyy/MM/dd HH:mm")
                    .format(new Date(file.lastModified())));
            if (file.isFile()) {
                long size = file.length() / 1024;
                size = size == 0 ? 1 : size;
                fileDetail.setSize(size);// KB
                String fileName = file.getName();
                String pic = "";
                String tmp = fileName.substring(fileName.lastIndexOf('.') + 1);
                switch (tmp) {
                    case "gz":
                        pic = "gz.png";
                        break;
                    case "img":
                        pic = "img.png";
                        break;
                    case "png":
                        pic = "pic.png";
                        break;
                    case "jpg":
                        pic = "pic.png";
                        break;
                    default:
                        pic = "other.png";
                        break;
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

    /**
     * 列出指定目录下的文件和目录
     *
     * @param path
     * @return
     */
    @RequestMapping(value = "/extresources/listfiles")
    public ReturnObject getFilesList(@RequestParam("path") String path) {
        File listPath = new File(path);

        ReturnObject re = new ReturnObject();
        re.setErr("0");
        if (listPath.isFile()) {
            String erro = path + " is a file";
            logger.error(erro);
            re.setErr("1");
            re.setMsg(erro);
            return re;
        }
        if (!listPath.exists()) {
            String erro = path + " no exist";
            logger.error(erro);
            re.setErr("1");
            re.setMsg(erro);
            return re;
        }
        List<FileDetail> reFiles = listFiles(listPath);
        re.setResult(reFiles);
        return re;
    }

    /**
     * 列出指定目录下的文件和目录
     *
     * @param pathOrFile
     * @return
     */
    @RequestMapping(value = "/extresources/deleteFileOrPath")
    public ReturnObject deletePathOrFile(
            @RequestParam("pathOrFile") String pathOrFile) {
        File delete = new File(pathOrFile);

        ReturnObject re = new ReturnObject();
        re.setErr("0");
        if (!delete.exists()) {
            String erro = pathOrFile + " no exist";
            logger.error(erro);
            re.setErr("1");
            re.setMsg(erro);
            return re;
        }
        try {
            if (delete.isDirectory()) {
                FileUtils.deleteDirectory(delete);
            } else {
                if (delete.delete()) {
                    logger.info("删除成功");
                }
            }
            re.setMsg("deleted");
        } catch (IOException e) {
            logger.error(e);
            re.setErr("1");
            re.setMsg("FAIL to delete " + pathOrFile);
        }
        return re;
    }

    /**
     * 上传镜像文件
     *
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/extresources/upload-file")
    public String uploadFile(HttpServletRequest request) throws IOException {
        String filename = request.getParameter("name");
        String path = request.getParameter("path");
        InputStream inputStream = null;
        OutputStream outputStream = null;
        File fileTmp = null;
        try {
            File savePath = new File(path);
            if (!savePath.exists() && savePath.mkdirs()) {
                logger.info("修改目录名称成功！");
            }
            File file = new File(path + File.separator + filename);
            fileTmp = new File(path + File.separator + filename + ".tmp");

            inputStream = request.getInputStream();
            outputStream = new FileOutputStream(fileTmp);

            int byteCount = 0;

            byte[] bytes = new byte[1024 * 1024];

            while ((byteCount = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, byteCount);
            }

            if (file.delete()) {
                logger.info("删除成功");
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

        return "ok";
    }
}
