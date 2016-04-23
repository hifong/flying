package {$PackageName}.service;
import java.util.List;
import java.util.Map;

import {$PackageName}.entity.{$ClassName};

public interface {$ClassName}Service{
	
	public {$ClassName} create({$ClassName} instance);
    
	public int update({$ClassName} instance);

	public int delete({$pk.propertyType} id);

	public {$ClassName} findByPrimaryKey(String id);

    public int getSize({$ClassName} instance);

	public List<{$ClassName}> list({$ClassName} condition, int start,
			int pageSize);
			
	public List<{$ClassName}> listAll({$ClassName} condition);

    public int getSize(Map<String, Object> instance);

	public List<{$ClassName}> list(Map<String, Object> condition, int start,
			int pageSize);
}