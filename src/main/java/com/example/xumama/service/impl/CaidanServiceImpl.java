package com.example.xumama.service.impl;

import cn.hutool.core.text.StrSplitter;
import com.example.xumama.entity.Caidan;
import com.example.xumama.entity.Peicai;
import com.example.xumama.entity.Qingcai;
import com.example.xumama.entity.Tangshui;
import com.example.xumama.entity.Zhucai;
import com.example.xumama.mapper.CaidanMapper;
import com.example.xumama.mapper.PeicaiMapper;
import com.example.xumama.mapper.QingcaiMapper;
import com.example.xumama.mapper.TangshuiMapper;
import com.example.xumama.mapper.ZhucaiMapper;
import com.example.xumama.service.CaidanService;
import com.example.xumama.util.DateUtil;
import com.example.xumama.vo.CaidanVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * CaidanServiceImpl
 *
 * @author zhangshun
 * @date 2022/8/8
 */
@Service
@Slf4j
public class CaidanServiceImpl implements CaidanService {

    private CaidanMapper caidanMapper;
    private ZhucaiMapper zhucaiMapper;
    private QingcaiMapper qingcaiMapper;
    private PeicaiMapper peicaiMapper;
    private TangshuiMapper tangshuiMapper;

    @Autowired
    public void setCaidanMapper(CaidanMapper caidanMapper) {
        this.caidanMapper = caidanMapper;
    }
    @Autowired
    public void setZhucaiMapper(ZhucaiMapper zhucaiMapper) {
        this.zhucaiMapper = zhucaiMapper;
    }
    @Autowired
    public void setQingcaiMapper(QingcaiMapper qingcaiMapper) {
        this.qingcaiMapper = qingcaiMapper;
    }
    @Autowired
    public void setPeicaiMapper(PeicaiMapper peicaiMapper) {
        this.peicaiMapper = peicaiMapper;
    }
    @Autowired
    public void setTangshuiMapper(TangshuiMapper tangshuiMapper) {
        this.tangshuiMapper = tangshuiMapper;
    }

    /**
     * 获取今日菜单
     *
     * @return 返回结果
     * @author zhangShun 2022/8/8
     */
    @Override
    public CaidanVo getCaidan() {
        Caidan caidan = caidanMapper.selectCaidanToday();
        List<Zhucai> zhucais = new ArrayList<>();
        List<Qingcai> qingcais = new ArrayList<>();
        List<Peicai> peicais = new ArrayList<>();
        List<Tangshui> tangshuis = new ArrayList<>();
        if(caidan != null){
            //主菜
            if(caidan.getCaidanZhucai() != null && caidan.getCaidanZhucai().length()>0){
                List<String> list = StrSplitter.split(caidan.getCaidanZhucai(),";",0,true,true);
                if(list != null){
                    for (String str : list){
                        Zhucai zhucai = zhucaiMapper.selectByPrimaryKey(Integer.parseInt(str));
                        zhucais.add(zhucai);
                    }
                }
            }
            //青菜
            if(caidan.getCaidanQingcai() != null && caidan.getCaidanQingcai().length()>0){
                List<String> list = StrSplitter.split(caidan.getCaidanQingcai(),";",0,true,true);
                if(list != null){
                    for (String str : list){
                        Qingcai qingcai = qingcaiMapper.selectByPrimaryKey(Integer.parseInt(str));
                        qingcais.add(qingcai);
                    }
                }
            }
            //配菜
            if(caidan.getCaidanPeicai() != null && caidan.getCaidanPeicai().length()>0){
                List<String> list = StrSplitter.split(caidan.getCaidanPeicai(),";",0,true,true);
                if(list != null){
                    for (String str : list){
                        Peicai peicai = peicaiMapper.selectByPrimaryKey(Integer.parseInt(str));
                        peicais.add(peicai);
                    }
                }
            }
            //糖水
            if(caidan.getCaidanTangshui() != null && caidan.getCaidanTangshui().length()>0){
                List<String> list = StrSplitter.split(caidan.getCaidanTangshui(),";",0,true,true);
                if(list != null){
                    for (String str : list){
                        Tangshui tangshui = tangshuiMapper.selectByPrimaryKey(Integer.parseInt(str));
                        tangshuis.add(tangshui);
                    }
                }
            }
        }
        if(zhucais.size()+qingcais.size()+peicais.size()+tangshuis.size()>0){
            CaidanVo caidanVo = new CaidanVo();
            caidanVo.setZhucais(zhucais);
            caidanVo.setQingcais(qingcais);
            caidanVo.setPeicais(peicais);
            caidanVo.setTangshuis(tangshuis);
            return caidanVo;
        }else {
            return null;
        }
    }

    /**
     * 获取所有菜单
     *
     * @return 返回结果
     * @author zhangShun 2022/8/9
     */
    @Override
    public CaidanVo getAllCaidan() {
        List<Zhucai> zhucais = zhucaiMapper.selectAll();
        List<Qingcai> qingcais = qingcaiMapper.selectAll();
        List<Peicai> peicais = peicaiMapper.selectAll();
        List<Tangshui> tangshuis = tangshuiMapper.selectAll();
        CaidanVo caidanVo = new CaidanVo();
        caidanVo.setZhucais(zhucais);
        caidanVo.setQingcais(qingcais);
        caidanVo.setPeicais(peicais);
        caidanVo.setTangshuis(tangshuis);
        return caidanVo;
    }

    @Override
    public void addCaidan(String[] zhucai, String[] qingcai, String[] peicai, String[] tangshui) {
        Caidan caidan = new Caidan();
        caidan.setCaidanDate(DateUtil.getDate());
        caidan.setCaidanZhucai(formatting(zhucai));
        caidan.setCaidanQingcai(formatting(qingcai));
        caidan.setCaidanPeicai(formatting(peicai));
        caidan.setCaidanTangshui(formatting(tangshui));
        log.info("addCaidan =>> {}" ,caidan);
        caidanMapper.insert(caidan);
    }

    @Override
    public int deleteCaidan() {
        return caidanMapper.deleteCaidan();
    }

    private String formatting(String[] cai) {
        if(cai != null && cai.length>0){
            StringBuilder stringBuilder = new StringBuilder();
            for (String str : cai){
                if(stringBuilder.length()>0){
                    stringBuilder.append(";");
                }
                stringBuilder.append(str);
            }
            return stringBuilder.toString();
        }else {
            return null;
        }
    }
}
