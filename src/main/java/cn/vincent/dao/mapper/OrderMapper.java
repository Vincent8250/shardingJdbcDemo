package cn.vincent.dao.mapper;

import cn.vincent.dao.entity.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    @Insert("INSERT INTO t_order  ( product_id, product_name, quantity, price, total_price )  VALUES(#{productId}, #{productName}, ${quantity}, ${price}, ${totalPrice} )")
    int insertOrder(Order order);
}
