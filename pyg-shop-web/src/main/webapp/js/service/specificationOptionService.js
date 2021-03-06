// 服务层
app.service('specificationOptionService', function($http) {
	    	
    // 读取列表数据绑定到表单中
    this.findAll = function () {
        return $http.get('../specificationOption/findAll');
    };
    // 分页
    this.findPage = function (page, rows) {
        return $http.get('../specificationOption/findPage/' + page + '/' + rows);
    };
    // 查询实体
    this.findOne = function (id) {
        return $http.get('../specificationOption/findOne/' + id);
    };
    // 增加
    this.add = function (entity) {
        return $http.post('../specificationOption/add', entity);
    };
    // 修改
    this.update = function (entity) {
        return $http.post('../specificationOption/update', entity);
    };
    // 删除
    this.del = function (ids) {
        return $http.get('../specificationOption/delete/' + ids);
    };
    // 搜索
    this.search = function (page, rows, searchEntity) {
        return $http.post('../specificationOption/search/' + page + "/" + rows, searchEntity);
    };

});
