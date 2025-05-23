package com.coinue.service.impl;

import com.coinue.model.PaymentReminder;
import com.coinue.service.ReminderService;
import com.coinue.util.DataManager;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 还款提醒服务实现类
 * 实现还款提醒相关的所有业务逻辑
 */
public class ReminderServiceImpl implements ReminderService {
    
    // 还款提醒数据缓存
    private List<PaymentReminder> reminderCache;
    
    /**
     * 构造函数，初始化时加载还款提醒数据
     */
    public ReminderServiceImpl() {
        this.reminderCache = DataManager.loadReminders();
    }

    @Override
    public List<PaymentReminder> getAllReminders() {
        return new ArrayList<>(reminderCache);
    }

    @Override
    public boolean addReminder(PaymentReminder reminder) {
        if (reminder == null) {
            return false;
        }
        
        // 确保ID唯一
        if (reminder.getId() == null || reminder.getId().isEmpty()) {
            reminder.setId(generateReminderId());
        } else if (reminderCache.stream().anyMatch(r -> r.getId().equals(reminder.getId()))) {
            return false; // ID冲突
        }
        
        boolean added = reminderCache.add(reminder);
        if (added) {
            saveReminders(reminderCache);
        }
        return added;
    }

    @Override
    public boolean updateReminder(PaymentReminder reminder) {
        if (reminder == null || reminder.getId() == null || reminder.getId().isEmpty()) {
            return false;
        }
        
        Optional<PaymentReminder> existingReminder = reminderCache.stream()
            .filter(r -> r.getId().equals(reminder.getId()))
            .findFirst();
        
        if (existingReminder.isPresent()) {
            int index = reminderCache.indexOf(existingReminder.get());
            reminderCache.set(index, reminder);
            saveReminders(reminderCache);
            return true;
        }
        
        return false;
    }

    @Override
    public boolean deleteReminder(String reminderId) {
        if (reminderId == null || reminderId.isEmpty()) {
            return false;
        }
        
        boolean removed = reminderCache.removeIf(reminder -> reminder.getId().equals(reminderId));
        if (removed) {
            saveReminders(reminderCache);
        }
        return removed;
    }

    @Override
    public List<PaymentReminder> getUpcomingReminders(int days) {
        LocalDate today = LocalDate.now();
        LocalDate deadline = today.plusDays(days);
        
        return reminderCache.stream()
            .filter(reminder -> {
                LocalDate dueDate = reminder.getDueDate();
                return !dueDate.isBefore(today) && !dueDate.isAfter(deadline);
            })
            .collect(Collectors.toList());
    }

    @Override
    public int calculateDaysRemaining(PaymentReminder reminder) {
        if (reminder == null || reminder.getDueDate() == null) {
            return -1;
        }
        
        LocalDate today = LocalDate.now();
        LocalDate dueDate = reminder.getDueDate();
        
        // 如果已经过期，返回负值
        if (dueDate.isBefore(today)) {
            return (int) ChronoUnit.DAYS.between(dueDate, today) * -1;
        }
        
        // 返回剩余天数
        return (int) ChronoUnit.DAYS.between(today, dueDate);
    }

    @Override
    public boolean saveReminders(List<PaymentReminder> reminders) {
        if (reminders != null) {
            try {
                DataManager.saveReminders(reminders);
                this.reminderCache = new ArrayList<>(reminders);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public List<PaymentReminder> getRemindersByType(String type) {
        if (type == null || type.isEmpty()) {
            return new ArrayList<>();
        }
        
        return reminderCache.stream()
            .filter(reminder -> type.equals(reminder.getType()))
            .collect(Collectors.toList());
    }
    
    /**
     * 生成新的提醒ID
     * @return 生成的唯一ID
     */
    private String generateReminderId() {
        return "reminder_" + System.currentTimeMillis();
    }
    
    /**
     * 刷新还款提醒数据缓存
     */
    public void refreshReminderCache() {
        this.reminderCache = DataManager.loadReminders();
    }
} 