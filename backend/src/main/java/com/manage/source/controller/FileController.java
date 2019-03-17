package com.manage.source.controller;

import com.manage.common.APIResponse;
import com.manage.common.BaseController;
import com.manage.common.Constants;
import com.manage.common.UrlConstants;
import com.manage.exception.impl.BizExceptionStatusEnum;
import com.manage.exception.impl.SysExceptionStatusEnum;
import com.manage.source.bean.FileDetail;
import com.manage.source.bean.MVDirBean;
import com.manage.source.service.FileService;
import com.manage.system.bean.DepartBean;
import com.manage.system.model.SysPermissionDto;
import com.manage.system.service.DepartService;
import com.manage.system.service.RolePermService;
import com.manage.util.file.FileUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by luya on 2018/6/11.
 */
@RestController
@RequestMapping(value = UrlConstants.URL_FILE_MODEL)
public class FileController extends BaseController {
    private Log logger = LogFactory.getLog(FileController.class);
    private Log bizLogger = LogFactory.getLog("bizLogger");
    // 系统文件根目录
    @Value("${file.fileRootPath}")
    private String fileRootPath;

    @Autowired
    private DepartService departService;

    @Autowired
    private RolePermService rolePermService;

    @Autowired
    private FileService fileService;

    /*根据菜单id和用户角色获取用户在当前部门内的根目录*/
    @RequestMapping(value = "/getBasePath", method = RequestMethod.GET)
    public APIResponse getRootpath(int menuId) {
        // 获取部门编码
        DepartBean departBean = departService.getDepartByMenuId(menuId);

        String departPath = fileRootPath + File.separator + departBean.getCode();
        // 创建路径
        File rootPath = new File(departPath);
        if (!rootPath.exists()) {
            fileService.makeDirs(departPath);
        }
        return APIResponse.toOkResponse(rootPath.getPath());
    }

    /*根据菜单id和用户角色获取用户在当前部门内的根目录*/
    private List<SysPermissionDto> getUserPermistion(int menuId) {
        try {
            // 获取权限
            if (StringUtils.isEmpty(menuId)) {
                // 参数不完整
                throw new Exception(SysExceptionStatusEnum.BAD_REQUEST.getMessage());
            }
            String account = super.getAccount();
            // 获取用户当前部门权限列表
            List<SysPermissionDto> permissionDtoList = rolePermService.getPermByRoleIdMenuId(menuId, account);
            return permissionDtoList;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        }
        return null;
    }

    @RequestMapping(value = "/fileList")
    public APIResponse fileList(@RequestParam("menuId") Integer menuId, @RequestParam("path") String path) {
        bizLogger.info(new StringBuilder().append("账号：").append(getAccount()).append("　　查询文件列表.路径:").append(path));

        // 如果是根目录不进行操作
        if (fileRootPath.contains(path)) {
            return APIResponse.toExceptionResponse(BizExceptionStatusEnum.FILE_NOT_EXEC_ERROR);
        }
        // 获取角色对应部门权限
        List<SysPermissionDto> permissionDtoList = this.getUserPermistion(menuId);
        if (permissionDtoList != null) {
            // 查看权限
            boolean searchFlg = false;

            for (SysPermissionDto dto : permissionDtoList) {
                // 拥有查看权限
                if (Constants.PERM_TYPE_SELECT.equals(dto.getName())) {
                    searchFlg = true;
                    break;
                }
            }
            // 获取列表
            if (searchFlg) {
                // 获取文件列表
                File listPath = new File(path);
                // TODO 如果不是文件
                if (listPath.isFile()) {
                    String erro = path + " is a file";
                    logger.error(erro);
                    return APIResponse.toExceptionResponse(BizExceptionStatusEnum.FILE_NOT_DIR_ERROR);
                }
                if (!listPath.exists()) {
                    String erro = path + " no exist";
                    logger.error(erro);
                    return APIResponse.toExceptionResponse(BizExceptionStatusEnum.FILE_NOT_EXIST_ERROR);
                }

                List<FileDetail> reFiles = fileService.listAdminFiles(listPath);
                return APIResponse.toOkResponse(reFiles);
            }else {
                return APIResponse.toExceptionResponse(BizExceptionStatusEnum.USER_HAS_NO_ROLE_ERROR);
            }
        }

        return APIResponse.toExceptionResponse(BizExceptionStatusEnum.FILE_NOT_EXEC_ERROR);
    }

    /**
     * 上传文件
     *
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/upload-file")
    public APIResponse uploadFile(HttpServletRequest request) throws Exception {
        String filename = request.getParameter("name");
        String path = request.getParameter("path");
        String menuIdStr = request.getParameter("menuId");
        bizLogger.info(new StringBuilder().append("账号：").append(getAccount()).append(" 上传文件路径:").append(path).append("文件名:").append(filename));
        Integer menuId = 0;
        //  转换menuid
        try {
            menuId = Integer.parseInt(menuIdStr);
        } catch (Exception e) {
            throw new Exception(SysExceptionStatusEnum.BAD_REQUEST.getMessage());
        }

        // 如果是根目录不进行操作
        if (fileRootPath.contains(path)) {
            throw new Exception(BizExceptionStatusEnum.FILE_NOT_EXEC_ERROR.getMessage());
        }

        // 获取角色对应部门权限
        List<SysPermissionDto> permissionDtoList = getUserPermistion(menuId);
        if (permissionDtoList != null) {
            for (SysPermissionDto dto : permissionDtoList) {
                if (Constants.PERM_TYPE_UPLOAD.equals(dto.getName())) {
                    fileService.uploadFile(request, path, filename);
                    return APIResponse.toOkResponse();
                }
            }
        }
        throw new Exception(BizExceptionStatusEnum.FILE_NOT_EXEC_ERROR.getMessage());
    }

    // 文件下载,表示/upload后面接的任何路径都会进入到这里
    @RequestMapping("/download")
    public APIResponse downloadFile(HttpServletResponse response, HttpServletRequest request,
                                    String paths, Integer menuId) {

        bizLogger.info(new StringBuilder().append("账号：").append(getAccount()).append(" 下载文件:").append(paths));

        // 获取角色对应部门权限
        List<SysPermissionDto> permissionDtoList = getUserPermistion(menuId);
        if (permissionDtoList != null) {
            for (SysPermissionDto dto : permissionDtoList) {
                if (Constants.PERM_TYPE_DOWNLOAD.equals(dto.getName())) {
                    String[] path = paths.split("~");
                    for (String filePath : path) {
                        File file = new File(filePath);
                        BufferedOutputStream bos = null;
                        FileInputStream fs = null;
                        try {
                            if (file.exists()) {

                                //String fileName = filePath.substring(filePath.lastIndexOf("\\") + 1);
                                String fileName = file.getName();
                                String userAgent = request.getHeader("User-Agent");
                                /* IE 8 至 IE 10 *//* IE 11 */
                                if (userAgent.toUpperCase().contains("MSIE") || userAgent.contains("Trident/7.0")) {
                                    fileName = URLEncoder.encode(fileName, "UTF-8");
                                } else if (userAgent.toUpperCase().contains("MOZILLA") || userAgent.toUpperCase().contains("CHROME")) {
                                    fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
                                } else {
                                    fileName = URLEncoder.encode(fileName, "UTF-8");
                                }
                                response.setCharacterEncoding("UTF-8");
                                response.setContentType("application/octet-stream");
                                response.setHeader("Content-type", "text/html;charset=UTF-8");
                                // 设置下载文件的名称,如果想直接在想查看就注释掉，因为要是文件原名才能下载，不然就只能在浏览器直接浏览无法下载
                                response.setHeader("content-disposition", "attachment;filename=" + fileName);

                                bos = new BufferedOutputStream(response.getOutputStream());
                                fs = new FileInputStream(file);
                                byte[] b = new byte[1024];
                                int len = 0;
                                while ((len = fs.read(b)) > 0) {
                                    bos.write(b, 0, len);
                                }
                                //刷新缓冲，写出
                                bos.flush();
                                fs.close();
                                bos.close();
                                logger.debug("文件下载成功。");
                            } else {
                                response.sendError(404);
                            }
                        } catch (Exception e) {
                            try {
                                bos.close();
                                fs.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            e.printStackTrace();
                            return APIResponse.toExceptionResponse(SysExceptionStatusEnum.SERVER_ERROR);
                        }
                    }
                    return APIResponse.toOkResponse();
                }
            }
        }
        // 没有权限返回
        return APIResponse.toExceptionResponse(BizExceptionStatusEnum.FILE_NOT_EXEC_ERROR);
    }

    @RequestMapping(value = "/delete")
    public APIResponse deleteFile(@RequestParam("paths") String paths, @RequestParam("menuId") Integer menuId) {

        bizLogger.info(new StringBuilder().append("账号：").append(getAccount()).append(" 删除文件:").append(paths));
        // 空文件删除
        if (StringUtils.isEmpty(paths)) {
            return APIResponse.toOkResponse();
        }

        List<SysPermissionDto> permissionDtoList = getUserPermistion(menuId);
        if (permissionDtoList != null) {
            for (SysPermissionDto dto : permissionDtoList) {
                if (Constants.PERM_TYPE_DELETE.equals(dto.getName())) {
                    String[] dirs = paths.split("<>");
                    List<String> errorFileList = new ArrayList<>();
                    for (String dir : dirs) {
                        if (!fileService.deleteFile(dir)) {
                            errorFileList.add(dir.substring(dir.lastIndexOf(File.separator) + 1));
                        }
                    }
                    return APIResponse.toOkResponse(errorFileList);
                }
            }
        }
        return APIResponse.toExceptionResponse(BizExceptionStatusEnum.FILE_NOT_EXEC_ERROR);
    }

    /* 移动文件*/
    @RequestMapping(value = "/changeFolder")
    public APIResponse changeFolder(@RequestParam("sourceDir") String sourceDir, @RequestParam("targetDir") String targetDir,
                                    @RequestParam("menuId") Integer menuId) {

        bizLogger.info(new StringBuilder().append("账号：").append(getAccount()).append(" 移动文件:")
                .append(sourceDir).append(" 到目录 ").append(targetDir));

        // 获取角色对应部门权限
        List<SysPermissionDto> permissionDtoList = this.getUserPermistion(menuId);
        if (permissionDtoList != null) {
            // 移动权限
            boolean mvFlg = false;
            // 有上传文件权限的才可移动
            for (SysPermissionDto dto : permissionDtoList) {
                // 拥有上传权限
                if (Constants.PERM_TYPE_UPLOAD.equals(dto.getName())) {
                    mvFlg = true;
                    break;
                }
            }
            // 获取列表
            if (mvFlg) {
                File sourceFile = new File(sourceDir);
                File targetFile = new File(targetDir + File.separator + sourceFile.getName());
                if (sourceFile.exists() && !targetFile.exists()) {
                    sourceFile.renameTo(targetFile);
                } else {
                    return APIResponse.toExceptionResponse(BizExceptionStatusEnum.FILE_EXIT_ERROR);
                }
                return APIResponse.toOkResponse();
            }
        }
         return APIResponse.toExceptionResponse(BizExceptionStatusEnum.USER_HAS_NO_ROLE_ERROR);
    }

    /*文件移动前，获取当前部门内的所有文件夹*/
    @RequestMapping("/getFolderList")
    public APIResponse getFolderList( @RequestParam("menuId") Integer menuId, @RequestParam("path") String rootPath) {
        // 获取角色对应部门权限
        List<SysPermissionDto> permissionDtoList = this.getUserPermistion(menuId);
        List<MVDirBean> reFiles = new LinkedList<>();
        if (permissionDtoList != null) {
            // 删除权限
            boolean delFlg = false;

            for (SysPermissionDto dto : permissionDtoList) {
                // 拥有删除权限
                if (Constants.PERM_TYPE_DELETE.equals(dto.getName())) {
                    delFlg = true;
                    break;
                }
            }

            // 获取列表
            if (delFlg) {
                LinkedList<MVDirBean> fileLinkList = new LinkedList();
                MVDirBean rootBean = new MVDirBean();
                rootBean.setId(rootPath);
                rootBean.setParent("#");
                rootBean.setText("");
                fileLinkList.add(rootBean);
                reFiles = fileService.getDirs(fileLinkList, rootPath);
            }
        }
        return APIResponse.toOkResponse(reFiles);

    }

    @RequestMapping(value = "/findByName")
    public APIResponse searchFileByName(@RequestParam("menuId") Integer menuId,@RequestParam("fileName") String fileName){

        // 获取角色对应部门权限
        List<SysPermissionDto> permissionDtoList = this.getUserPermistion(menuId);
        List<FileDetail> reFiles = new LinkedList<>();
        if (permissionDtoList != null) {
            // 查看权限
            boolean searchFlg = false;

            for (SysPermissionDto dto : permissionDtoList) {
                // 拥有查看权限
                if (Constants.PERM_TYPE_SELECT.equals(dto.getName())) {
                    searchFlg = true;
                    break;
                }
            }

            // 获取部门编码
            DepartBean departBean = departService.getDepartByMenuId(menuId);
            // 获取文件查询路径
            String departPath = fileRootPath + File.separator + departBean.getCode();
            // 获取列表
            if (searchFlg) {
                LinkedList fileLinkList = new LinkedList();
                try {
                    reFiles = fileService.getFilesByFileDir(fileLinkList, departPath,fileName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return APIResponse.toOkResponse(reFiles);
    }

    @RequestMapping(value = "/newdir")
    public APIResponse newFolder(@RequestParam("path") String path, @RequestParam("dir") String dir,
                                 @RequestParam("menuId") Integer menuId) {
        bizLogger.info(new StringBuilder().append("账号：").append(getAccount()).append(" 创建文件夹:").append(path).append(File.separator).append(dir));
        try {
            // 获取上传权限
            List<SysPermissionDto> permissionDtoList = getUserPermistion(menuId);
            if (permissionDtoList != null) {
                for (SysPermissionDto dto : permissionDtoList) {
                    // 有权限可以创建文件夹
                    if (Constants.PERM_TYPE_UPLOAD.equals(dto.getName())) {
                        if (!StringUtils.isEmpty(path) && !StringUtils.isEmpty(dir)) {
                            fileService.makeDirs(path + File.separator + dir);
                            return APIResponse.toOkResponse();
                        }
                    }
                }
            }
            return APIResponse.toExceptionResponse(BizExceptionStatusEnum.FILE_NOT_EXEC_ERROR);
        } catch (Exception e) {
            return APIResponse.toExceptionResponse(BizExceptionStatusEnum.FILE_CREATE_ERROR);
        }
    }

}
