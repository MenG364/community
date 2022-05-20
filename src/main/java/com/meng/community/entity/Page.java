package com.meng.community.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: 封装分页相关的信息
 * Created by MenG on 2022/5/20 21:11
 */


public class Page {
    //当前的页码
    private int current=1;

    //显示的上限
    private int limit=10;

    //数据的总数，用于计算总的页数
    private int rows;

    //查询路径，用于复用分页的链接
    private String path;

    public Page() {
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (this.current>=1){
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit>=1 && limit<=100){

            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows>=0){
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Page{" +
                "current=" + current +
                ", limit=" + limit +
                ", rows=" + rows +
                ", path='" + path + '\'' +
                '}';
    }

    /**
     *
     * @return 获取当前页的起始行
     */
    public int getOffset(){
        return (current-1)*limit;
    }

    /**
     *
     * @return 获取总的页数
     */
    public int getTotal(){
        return rows%limit==0?rows/limit:rows/limit+1;
    }

    /**
     *
     * @return 获取起始页码
     */
    public int getFrom(){
        int from=current-2;
        return Math.max(from, 1);
    }

    /**
     *
     * @return 获取终止页码
     */
    public int getTo(){
        int to=current+2;
        return Math.min(to, getTotal());
    }
}
