package cn.vincent.controller;

import cn.vincent.dao.entity.Order;
import cn.vincent.dao.mapper.OrderMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/h")
public class HomeController {

    @Autowired
    OrderMapper orderMapper;

    @GetMapping("/getAll")
    public String getAll() throws JsonProcessingException {
        List<Order> orders = orderMapper.selectList(new QueryWrapper<Order>());
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(orders);
    }

    @GetMapping("/getById")
    public String getById() throws JsonProcessingException {
        List<Long> ids =new ArrayList<Long>(){
            {
                add(882190860622495745L);//1
                add(882190860677021697L);//5
                add(882190860710576129L);//7
                add(882190860639272960L);//2
                add(882190860697993216L);//6
            }
        };
        List<Order> orders = orderMapper.selectBatchIds(ids);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(orders);
    }

    @GetMapping("/add")
    public void insert() {
        for (int i = 0; i < 10; i++) {
            Order order = Order.builder()
                    .productId("00" + i)
                    .productName("帽子 款式：" + i)
                    .price(new BigDecimal(500))
                    .quantity(2 * i)
                    .totalPrice(new BigDecimal(500 * 2 * i))
                    .build();
            orderMapper.insertOrder(order);
        }
    }
}