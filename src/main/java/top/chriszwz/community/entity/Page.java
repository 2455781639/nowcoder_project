package top.chriszwz.community.entity;

/*
 * @Description: 封装分页相关的信息
 * @Author: Chris(张文卓)
 * @Date: 2022/5/21 16:03
 */
public class Page {

    //页码
    private int current = 1;
    //一页的帖子数量上限
    private int limit = 10;
    //数据条数
    private int rows;
    //查询路径，用于页面复用链接
    private String path = "";

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current >= 1){
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit >= 1 && limit <= 100){
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if(rows >= 0){
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    //页面起始行
    public int getOffset(){
        return (current - 1) * limit;
    }

    //页面数量
    public int getTotal(){
        if(rows % limit == 0){
            return rows / limit;
        }else{
            return rows / limit + 1;
        }
    }

    public int getFrom(){
        int from = current - 2;
        return Math.max(from, 1);
    }

    public int getTo(){
        int to = current + 2;
        int total = getTotal();
        return Math.min(to, total);

    }
}
