package com.bjpowernode.vo;



public class PageInfo {

    // 页号
    private Integer pageNo ;
    // 每页大小
    private Integer pageSize ;
    // 总页数
    private Integer totalPages;
    // 总记录数
    private Integer totalRecords;

    public PageInfo(){
        this.pageNo = 1 ;
        this.pageSize = 9 ;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getTotalPages() {
        //计算总页数
        if( totalRecords % pageSize == 0 ){
            totalPages = totalRecords / pageSize;
        } else {
            totalPages = totalRecords / pageSize + 1;
        }
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getTotalRecords() {
        return totalRecords;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }
}
