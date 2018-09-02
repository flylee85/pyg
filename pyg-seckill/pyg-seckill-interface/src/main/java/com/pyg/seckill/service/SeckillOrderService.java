package com.pyg.seckill.service;
import java.util.List;
import com.pyg.pojo.TbSeckillOrder;

import com.pyg.vo.PageResult;

/**
 * Description: 服务层接口
 *
 * @author AK
 * @date 2018/8/9 10:00
 * @since 1.0.0
 */
public interface SeckillOrderService {

	/**
     * description: 返回全部列表
     *
     * @return java.util.List<com.pyg.pojo.TbSeckillOrder>
     * @author AK
     * @date  2018年08月09日 10:00:00
     */
	List<TbSeckillOrder> findAll();
		
	/**
     * description: 分页返回全部列表
     *
     * @param page 当前页面
     * @param rows 每页记录数
     * @return com.pyg.vo.PageResult
     * @author AK
     * @date  2018年08月09日 10:00:00
     */
	PageResult findPage(int page, int rows);
		
	/**
     * description: 增加
     *
     * @param seckillOrder 实体
     * @return com.pyg.vo.InfoResult
     * @author AK
     * @date  2018年08月09日 10:00:00
     */
	void add(TbSeckillOrder seckillOrder);
		
	/**
     * description: 修改
     *
     * @param seckillOrder 实体
     * @return com.pyg.vo.InfoResult
     * @author AK
     * @date  2018年08月09日 10:00:00
     */
	void update(TbSeckillOrder seckillOrder);
	
	/**
     * description: 根据id返回实体
     *
     * @param id 实体id
     * @return com.pyg.pojo.TbSeckillOrder
     * @author AK
     * @date  2018年08月09日 10:00:00
     */
	TbSeckillOrder findOne(Long id);	
	
	/**
     * description: 批量删除
     *
     * @param ids 实体ids
     * @return com.pyg.vo.InfoResult
     * @author AK
     * @date  2018年08月09日 10:00:00
     */
	void delete(Long[] ids);

	/**
     * description: 查询+分页
     *     
     * @param page 当前页
     * @param rows 每页记录数
     * @param seckillOrder 查询条件
     * @return com.pyg.vo.PageResult
     * @author AK
     * @date  2018年08月09日 10:00:00
     */
	PageResult findPage(Integer page, Integer rows, TbSeckillOrder seckillOrder);
	
}
