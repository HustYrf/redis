package com.learn.redis.FunnelRateLimiter;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName FunnelRateLimiter
 * @Descripition 单机版漏斗算法
 * @Author Administrator
 * @Date 2018/12/1 17:30
 **/
public class FunnelRateLimiter {
    static class Funnel {
        int capacity;
        float leakingRate;
        int leftQuota;
        long leakingTs;

        public Funnel(int capacity, float leakingRate) {
            this.capacity = capacity;
            this.leakingRate = leakingRate;
            this.leftQuota = capacity;
            this.leakingTs = System.currentTimeMillis();
        }


        void makeSpace() {
            long nowTs = System.currentTimeMillis();
            long deltaTs = nowTs - leakingTs;
            int deltaQuota = (int) (deltaTs * leakingRate);//放水量
            if (deltaQuota < 0) {//时间过长，int整数溢出
                this.leftQuota = capacity;
                this.leakingTs = nowTs;
                return;
            }
            if (deltaQuota < 1) {//过小
                return;
            }
            this.leftQuota += deltaQuota;
            this.leakingTs = nowTs;
            if (this.leftQuota > this.capacity) {
                this.leftQuota = this.capacity;
            }
        }

        boolean watering(int quota) {
            makeSpace();
            if (this.leftQuota > quota) {
                this.leftQuota -= quota;
                return true;
            }
            return false;
        }
    }

    private Map<String, Funnel> funnels = new HashMap<>();

    public boolean isActionAllowed(String userId, String actionKey, int capacity, float leakingRate) {
        String key = String.format("%s:%s", userId, actionKey);
        Funnel funnel = funnels.get(key);
        if (funnel == null) {
            funnel = new Funnel(capacity, leakingRate);
            funnels.put(key, funnel);
        }
        return funnel.watering(1); // 需要1个quota
    }

}
