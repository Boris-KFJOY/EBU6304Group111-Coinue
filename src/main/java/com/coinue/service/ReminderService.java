package com.coinue.service;

import com.coinue.model.PaymentReminder;

import java.time.LocalDate;
import java.util.List;

/**
 * 还款提醒服务接口
 * 负责处理所有与还款提醒相关的业务逻辑，包括添加、更新、删除、加载提醒等操作
 */
public interface ReminderService {
    
    /**
     * 获取所有还款提醒
     * @return 还款提醒列表
     */
    List<PaymentReminder> getAllReminders();
    
    /**
     * 添加还款提醒
     * @param reminder 还款提醒对象
     * @return 添加是否成功
     */
    boolean addReminder(PaymentReminder reminder);
    
    /**
     * 更新还款提醒
     * @param reminder 更新后的还款提醒对象
     * @return 更新是否成功
     */
    boolean updateReminder(PaymentReminder reminder);
    
    /**
     * 删除还款提醒
     * @param reminderId 还款提醒ID
     * @return 删除是否成功
     */
    boolean deleteReminder(String reminderId);
    
    /**
     * 获取即将到期的还款提醒
     * @param days 天数，如7表示7天内到期的提醒
     * @return 即将到期的还款提醒列表
     */
    List<PaymentReminder> getUpcomingReminders(int days);
    
    /**
     * 计算距离还款日期的剩余天数
     * @param reminder 还款提醒对象
     * @return 剩余天数
     */
    int calculateDaysRemaining(PaymentReminder reminder);
    
    /**
     * 保存所有还款提醒
     * @param reminders 还款提醒列表
     * @return 保存是否成功
     */
    boolean saveReminders(List<PaymentReminder> reminders);
    
    /**
     * 按类型过滤还款提醒
     * @param type 还款提醒类型
     * @return 指定类型的还款提醒列表
     */
    List<PaymentReminder> getRemindersByType(String type);
} 