package org.gradle.needle.mapper;

import java.util.List;

public interface SuperMapper {
	
	public List<Prodata> selectProdata(Prodata data);
	
	public List<Propaths> selectPropaths(Propaths config);
	
	public List<Pathdescr> selectPathdescr(Pathdescr descr);

}
