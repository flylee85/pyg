package com.pyg.manager.controller;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.pyg.pojo.TbItem;
import com.pyg.vo.Goods;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pojo.TbGoods;
import com.pyg.manager.service.GoodsService;

import com.pyg.vo.PageResult;
import com.pyg.vo.InfoResult;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * Description: controller
 * @author AK
 * @date 2018/8/9 10:00
 * @since 1.0.0
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference(timeout=100000)
	private GoodsService goodsService;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private ActiveMQTopic indexHtml;
	@Autowired
	private ActiveMQTopic deleteIndexHtml;
	@Autowired
	private ActiveMQTopic pygGenHtml;
	
	/**
     * description: 返回全部列表
     *
     * @return java.util.List<com.pyg.pojo.TbGoods>
     * @author AK
     * @date  2018年08月09日 10:00:00
     */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
     * description: 分页返回全部列表
     *
     * @param page 当前页面
     * @param rows 每页记录数
     * @return com.pyg.vo.PageResult
     * @author AK
     * @date  2018年08月09日 10:00:00
     */
	@RequestMapping("/findPage/{page}/{rows}")
	public PageResult findPage(@PathVariable Integer page, @PathVariable Integer rows){
		return goodsService.findPage(page, rows);
	}
	
	/**
     * description: 增加
     *
     * @param goods 实体
     * @return com.pyg.vo.InfoResult
     * @author AK
     * @date  2018年08月09日 10:00:00
     */
	@RequestMapping("/add")
	public InfoResult add(@RequestBody Goods goods){
		try {
			goodsService.add(goods);
			return new InfoResult(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new InfoResult(false, "增加失败");
		}
	}
	
	/**
     * description: 修改
     *
     * @param goods 实体
     * @return com.pyg.vo.InfoResult
     * @author AK
     * @date  2018年08月09日 10:00:00
     */
	@RequestMapping("/update")
	public InfoResult update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new InfoResult(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new InfoResult(false, "修改失败");
		}
	}	
	
	/**
     * description: 根据id返回实体
     *
     * @param id 实体id
     * @return com.pyg.pojo.TbGoods
     * @author AK
     * @date  2018年08月09日 10:00:00
     */
	@RequestMapping("/findOne/{id}")
	public Goods findOne(@PathVariable Long id){
		return goodsService.findOne(id);		
	}
	
	/**
     * description: 批量删除
     *
     * @param ids 实体ids
     * @return com.pyg.vo.InfoResult
     * @author AK
     * @date  2018年08月09日 10:00:00
     */
	@RequestMapping("/delete/{ids}")
	public InfoResult delete(@PathVariable Long[] ids){
		try {
			goodsService.delete(ids);
			List<String> itemIds = goodsService.findItemIds(ids);
			String jsonString = JSON.toJSONString(itemIds);
			jmsTemplate.send(deleteIndexHtml, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createTextMessage(jsonString);
				}
			});
			return new InfoResult(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new InfoResult(false, "删除失败");
		}
	}
	
	/**
     * description: 查询+分页
     *     
     * @param page 当前页
     * @param rows 每页记录数
     * @param goods 查询条件
     * @return com.pyg.vo.PageResult
     * @author AK
     * @date  2018年08月09日 10:00:00
     */
	@RequestMapping("/search/{page}/{rows}")
	public PageResult search(@PathVariable Integer page, @PathVariable Integer rows, @RequestBody TbGoods goods){
		return goodsService.findPage(page, rows, goods);		
	}

	/**
	 * description: 更新商品状态
	 *
	 * @param status 商品状态 0:未审核 1:审核通过 2:驳回 3:关闭
	 * @param ids 商品id
	 * @return com.pyg.vo.InfoResult
	 * @author AK
	 * @date  2018年08月16日 08:59:04
	 */
	@RequestMapping("/updateStatus/{status}/{ids}")
	public InfoResult updateStatus(@PathVariable String status, @PathVariable Long[] ids) {
		try {
			goodsService.updateStatus(status, ids);
			if("1".equals(status)) {
				List<TbItem> itemList = goodsService.findItemList(ids);
				String itemJson = JSON.toJSONString(itemList);
				jmsTemplate.send(indexHtml, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						return session.createTextMessage(itemJson);
					}
				});
				jmsTemplate.send(pygGenHtml, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						return session.createObjectMessage(ids);
					}
				});
			}
			return new InfoResult(true, "操作成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new InfoResult(false, "操作失败");
		}
	}
	
}
