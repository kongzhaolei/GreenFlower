package org.gradle.needle.mapper;

import java.util.List;
/**
 * 
 * @author kongzhaolei
 * 1. SuperMapper.xml中namespace等于mapper接口地址
 * 2. SuperMpper.java接口中的方法名和SuperMapper.xml中的statement的Id一致
 * 3. SuperMpper.java接口中的方法输入参数和SuperMapper.xml中的statement的parameterType类型一致
 * 4. SuperMpper.java接口中的方法返回值类型和SuperMapper.xml中的statement的resultType类型一致
 *
 */

public interface SuperMapper {
	
	public List<Prodata> selectProdata(Prodata data);
	
	public List<Propaths> selectPropaths(Propaths config);
	
	public List<Pathdescr> selectPathdescr(Pathdescr descr);
	
	public List<Portinfo> selectPortinfo(Portinfo portinfo); 

}
