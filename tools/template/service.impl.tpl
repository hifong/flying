package {$PackageName}.service.impl;

import java.util.List;
import java.util.Map;

import {$PackageName}.entity.{$ClassName};
import {$PackageName}.dao.{$ClassName}DAO;
import {$PackageName}.service.{$ClassName}Service;
import com.aspire.common.log.Logger;

public class {$ClassName}ServiceImpl implements {$ClassName}Service {
    private Logger logger = Logger.getLogger({$ClassName}ServiceImpl.class);
    
	private {$ClassName}DAO {$ClassName}DAO;
	
	public void set{$ClassName}DAO({$ClassName}DAO dao) {
		this.{$ClassName}DAO = dao;
	}
	
	public {$ClassName} create({$ClassName} instance) {
		return this.{$ClassName}DAO.create(instance);
	}
    
	public int update({$ClassName} instance) {
		return this.{$ClassName}DAO.update(instance);
	}

	public int delete({$pk.propertyType} id) {
		return this.{$ClassName}DAO.delete(id);
	}

	public {$ClassName} findByPrimaryKey(String id) {
		return this.{$ClassName}DAO.findByPrimaryKey(id);
	}

    public int getSize({$ClassName} instance) {
		return this.{$ClassName}DAO.getRowCountByProperty(instance);
	}

	public List<{$ClassName}> list({$ClassName} condition, int start,
			int pageSize) {
		return this.{$ClassName}DAO.list(condition, start, pageSize);
	}
	
	public List<{$ClassName}> listAll({$ClassName} condition) {
		return this.{$ClassName}DAO.listAll(condition);
	}

    public int getSize(Map<String, Object> instance) {
		return this.{$ClassName}DAO.getRowCountByMap(instance);
	}

	public List<{$ClassName}> list(Map<String, Object> condition, int start,
			int pageSize) {
		return this.{$ClassName}DAO.findByMap(condition, start, pageSize);
	}
}