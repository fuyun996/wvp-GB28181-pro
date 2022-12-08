package com.genersoft.iot.vmp.vmanager.openapi;

import com.genersoft.iot.vmp.common.StreamInfo;
import com.genersoft.iot.vmp.conf.UserSetting;
import com.genersoft.iot.vmp.conf.exception.ControllerException;
import com.genersoft.iot.vmp.gb28181.bean.Device;
import com.genersoft.iot.vmp.gb28181.bean.DeviceChannel;
import com.genersoft.iot.vmp.gb28181.transmit.callback.DeferredResultHolder;
import com.genersoft.iot.vmp.gb28181.transmit.callback.RequestMessage;
import com.genersoft.iot.vmp.media.zlm.dto.MediaServerItem;
import com.genersoft.iot.vmp.service.IDeviceService;
import com.genersoft.iot.vmp.service.IPlayService;
import com.genersoft.iot.vmp.service.IRoleDeviceChannelService;
import com.genersoft.iot.vmp.service.IUserService;
import com.genersoft.iot.vmp.storager.IVideoManagerStorage;
import com.genersoft.iot.vmp.storager.dao.dto.User;
import com.genersoft.iot.vmp.utils.IpUtils;
import com.genersoft.iot.vmp.utils.ServletUtils;
import com.genersoft.iot.vmp.vmanager.bean.BaseTree;
import com.genersoft.iot.vmp.vmanager.bean.DeferredResultEx;
import com.genersoft.iot.vmp.vmanager.bean.ErrorCode;
import com.genersoft.iot.vmp.vmanager.bean.WVPResult;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name  = "开放接口")
@CrossOrigin
@RestController
@RequestMapping("/api/openapi")
public class OpenApiController {

    private final static Logger logger = LoggerFactory.getLogger(OpenApiController.class);
    @Autowired
    private IVideoManagerStorage storager;

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IPlayService playService;

    @Autowired
    private DeferredResultHolder resultHolder;

    @Autowired
    private UserSetting userSetting;

    @Autowired
    private IRoleDeviceChannelService roleDeviceChannelService;


    /**
     * 分页查询国标设备
     * @param page 当前页
     * @param count 每页查询数量
     * @return 分页国标列表
     */
    @Operation(summary = "分页查询国标设备")
    @Parameter(name = "page", description = "当前页", required = true)
    @Parameter(name = "count", description = "每页查询数量", required = true)
    @Parameter(name = "keyword", description = "关键词", required = false)
    @GetMapping("/devicesList")
    public PageInfo devices(int page, int count, String keyword){
        HttpServletRequest request =  ServletUtils.getRequest();
        String pushkey = request.getHeader("pushKey");
        if("".equals(pushkey)){
            throw new ControllerException(102,"请提供pushkey!");
        }
        String serverIP = IpUtils.getIpAddr(request);
        User user = userService.getUserByPushKey(pushkey);
        if(user==null){
            throw new ControllerException(103,"请提供正确pushkey!");
        }
        String pushIp = user.getPushIp();
        if(isAccess(serverIP,pushIp)){
            int roleId = user.getRole().getId();
            return storager.queryVideoDeviceListByRoleId(page, count,keyword, roleId);
        }else {
            throw new ControllerException(101,"白名单验证失败！");
        }
    }

    /**
     * 查询国标树
     * @param deviceId 设备ID
     * @param parentId 父ID
     * @param page 当前页
     * @param count 每页条数
     * @return 国标设备
     */
    @Operation(summary = "查询国标树")
    @Parameter(name = "deviceId", description = "设备国标编号", required = true)
    @Parameter(name = "parentId", description = "父级国标编号")
    @Parameter(name = "onlyCatalog", description = "只获取目录")
    @Parameter(name = "page", description = "当前页", required = true)
    @Parameter(name = "count", description = "每页条数", required = true)
    @GetMapping("/tree/{deviceId}")
    public ResponseEntity<PageInfo> getTree(@PathVariable String deviceId,
                                            @RequestParam(required = false) String parentId,
                                            @RequestParam(required = false) Boolean onlyCatalog,
                                            int page, int count){

        HttpServletRequest request =  ServletUtils.getRequest();
        String pushkey = request.getHeader("pushKey");
        if("".equals(pushkey)){
            throw new ControllerException(102,"请提供pushkey!");
        }
        String serverIP = IpUtils.getIpAddr(request);
        User user = userService.getUserByPushKey(pushkey);
        if(user==null){
            throw new ControllerException(103,"请提供正确pushkey!");
        }
        String pushIp = user.getPushIp();
        if(isAccess(serverIP,pushIp)){
            if (page <= 0) {
                page = 1;
            }
            if (onlyCatalog == null) {
                onlyCatalog = false;
            }

            List<BaseTree<DeviceChannel>> treeData = deviceService.queryVideoDeviceTree(deviceId, parentId, onlyCatalog);
            if (treeData == null || (page - 1) * count > treeData.size()) {
                PageInfo<BaseTree<DeviceChannel>> pageInfo = new PageInfo<>();
                pageInfo.setPageNum(page);
                pageInfo.setTotal(treeData == null? 0 : treeData.size());
                pageInfo.setSize(0);
                pageInfo.setList(new ArrayList<>());
                return new ResponseEntity<>(pageInfo, HttpStatus.OK);
            }

            int toIndex = Math.min(page * count, treeData.size());
            // 处理分页
            List<BaseTree<DeviceChannel>> trees = treeData.subList((page - 1) * count, toIndex);
            PageInfo<BaseTree<DeviceChannel>> pageInfo = new PageInfo<>();
            pageInfo.setPageNum(page);
            pageInfo.setTotal(treeData.size());
            pageInfo.setSize(trees.size());
            pageInfo.setList(trees);

            return new ResponseEntity<>(pageInfo,HttpStatus.OK);
        }else {
            throw new ControllerException(101,"白名单验证失败！");
        }


    }

    @Operation(summary = "开始点播")
    @Parameter(name = "deviceId", description = "设备国标编号", required = true)
    @Parameter(name = "channelId", description = "通道国标编号", required = true)
    @GetMapping("/start/{deviceId}/{channelId}")
    public DeferredResult<WVPResult<StreamInfo>> play(HttpServletRequest request, @PathVariable String deviceId,
                                                      @PathVariable String channelId) {

        String pushkey = request.getHeader("pushKey");
        if("".equals(pushkey)){
            throw new ControllerException(102,"请提供pushkey!");
        }
        String serverIP = IpUtils.getIpAddr(request);
        User user = userService.getUserByPushKey(pushkey);
        if(user==null){
            throw new ControllerException(103,"请提供正确pushkey!");
        }
        Map rolechanneld = roleDeviceChannelService.getDeviceChannelByRoleIdAndChannelId(user.getRole().getId(),channelId);
        if(rolechanneld==null){
            throw new ControllerException(104,"当前用户无权限播放此视频!");
        }
        String pushIp = user.getPushIp();
        if(isAccess(serverIP,pushIp)){
            // 获取可用的zlm
            Device device = storager.queryVideoDevice(deviceId);
            MediaServerItem newMediaServerItem = playService.getNewMediaServerItem(device);

            RequestMessage msg = new RequestMessage();
            String key = DeferredResultHolder.CALLBACK_CMD_PLAY + deviceId + channelId;
            boolean exist = resultHolder.exist(key, null);
            msg.setKey(key);
            String uuid = UUID.randomUUID().toString();
            msg.setId(uuid);
            DeferredResult<WVPResult<StreamInfo>> result = new DeferredResult<>(userSetting.getPlayTimeout().longValue());
            DeferredResultEx<WVPResult<StreamInfo>> deferredResultEx = new DeferredResultEx<>(result);

            result.onTimeout(()->{
                logger.info("点播接口等待超时");
                // 释放rtpserver
                WVPResult<StreamInfo> wvpResult = new WVPResult<>();
                wvpResult.setCode(ErrorCode.ERROR100.getCode());
                wvpResult.setMsg("点播超时");
                msg.setData(wvpResult);
                resultHolder.invokeResult(msg);
            });

            if (userSetting.getUseSourceIpAsStreamIp()) {
                // TODO 在点播未成功的情况下在此调用接口点播会导致返回的流地址ip错误
                deferredResultEx.setFilter(result1 -> {
                    WVPResult<StreamInfo> wvpResult1 = (WVPResult<StreamInfo>)result1;
                    WVPResult<StreamInfo> clone = null;
                    try {
                        clone = (WVPResult<StreamInfo>)wvpResult1.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new RuntimeException(e);
                    }
                    if (clone.getCode() == ErrorCode.SUCCESS.getCode()) {
                        StreamInfo data = clone.getData().clone();
                        data.channgeStreamIp(request.getLocalName());
                        clone.setData(data);
                    }
                    return clone;
                });
            }

            // 录像查询以channelId作为deviceId查询
            resultHolder.put(key, uuid, deferredResultEx);

            if (!exist) {
                playService.play(newMediaServerItem, deviceId, channelId, null, null, null);
            }
            return result;
        }else{
            throw new ControllerException(101,"白名单验证失败！");
        }
    }

    public boolean isAccess(String serverIP,String pushIp){
        boolean flag = false;

        if("0.0.0.0".equals(pushIp)){
            flag = true;
        }else {
            if(serverIP.equals(pushIp)){
                flag = true;
            }
        }

        return flag;
    }
}
