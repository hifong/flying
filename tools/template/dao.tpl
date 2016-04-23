package {$PackageName}.dao;

import java.util.List;
import java.util.Map;

import com.aspire.common.ibatis.BaseDao;
import {$PackageName}.entity.{$ClassName};
import {$PackageName}.dao.{$ClassName}DAO;

@SuppressWarnings("unchecked")
public class {$ClassName}DAO extends BaseDao {
	
	public {$ClassName} create({$ClassName} instance) {
		return ({$ClassName})getSqlMapClientTemplate().insert("insert{$ClassName}", instance);
	}

	public int update({$ClassName} instance) {
		return getSqlMapClientTemplate().update("update{$ClassName}", instance);
	}

	public int delete({$pk.propertyType} id) {
		return getSqlMapClientTemplate().delete("delete{$ClassName}", id);
	}
	
	public {$ClassName} findByPrimaryKey(String id) {
		List<{$ClassName}> list = (List<{$ClassName}>)getSqlMapClientTemplate().queryForList("find{$ClassName}ByPrimaryKey",id);
		if(!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}
	
	public List<{$ClassName}> list({$ClassName} instance, int page, int size) {
		return (List<{$ClassName}>)getSqlMapClientTemplate().queryForList("find{$ClassName}ByProperty", instance, page, size);
	}
	
	public List<{$ClassName}> listAll({$ClassName} instance) {
		return (List<{$ClassName}>)getSqlMapClientTemplate().queryForList("find{$ClassName}ByProperty", instance);
	}
	
	public int getRowCountByProperty({$ClassName} instance) {
		Integer i = (Integer) this.getSqlMapClientTemplate().queryForObject("findRowCount{$ClassName}ByProperty", instance);
		return i;
	}
	
	public List<{$ClassName}> findByMap(Map<String, Object> instance, int page, int size) {
		return (List<{$ClassName}>)getSqlMapClientTemplate().queryForList("find{$ClassName}ByMap", instance, page, size);
	}
	
	public int getRowCountByMap(Map<String, Object> instance) {
		Integer i = (Integer) this.getSqlMapClientTemplate().queryForObject("findRowCount{$ClassName}ByMap", instance);
		return i;
	}
}