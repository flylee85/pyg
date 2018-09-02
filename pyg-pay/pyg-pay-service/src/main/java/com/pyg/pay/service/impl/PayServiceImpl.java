package com.pyg.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pyg.pay.service.PayService;
import com.pyg.util.HttpClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 微信支付接口实现类
 *
 * @author AK
 * @date 2018/8/28 0:01
 * @since 1.0.0
 */
@Service
public class PayServiceImpl implements PayService {
    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String partner;
    @Value("${partnerkey}")
    private String partnerkey;

    /**
     * description: 生成微信支付二维码
     *
     * @param out_trade_no 订单号
     * @param total_fee 金额(分)
     * @return java.util.Map
     * @author AK
     * @date  2018年08月28日 00:01:15
     */
    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        //1.创建参数
        Map<String,String> param = new HashMap();
        param.put("appid", appid); //公众号
        param.put("mch_id", partner); //商户号
        param.put("nonce_str", WXPayUtil.generateNonceStr()); //随机字符串
        param.put("body", "品优购"); //商品描述
        param.put("out_trade_no", out_trade_no); //商户订单号
        param.put("total_fee", "1"); //总金额（分）
        param.put("spbill_create_ip", "127.0.0.1"); //IP
        param.put("notify_url", "http://test.itcast.cn"); //回调地址(随便写)
        param.put("trade_type", "NATIVE"); //交易类型

        //2.生成要发送的xml
        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();

            //3.获得结果
            String result = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
            Map<String, String> map = new HashMap<>();
            map.put("code_url", resultMap.get("code_url"));//支付地址
            map.put("total_fee", total_fee);//总金额
            map.put("out_trade_no",out_trade_no);//订单号
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    /**
     * description: 查询支付状态
     *
     * @param out_trade_no 订单号
     * @return java.util.Map
     * @author AK
     * @date  2018年08月28日 00:28:32
     */
    @Override
    public Map queryPayStatus(String out_trade_no) {
        Map param = new HashMap();
        param.put("appid", appid); //公众账号ID
        param.put("mch_id", partner); //商户号
        param.put("out_trade_no", out_trade_no); //订单号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        String url = "https://api.mch.weixin.qq.com/pay/orderquery";
        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient client = new HttpClient(url);
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();
            String result = client.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(result);
            System.out.println(map);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}