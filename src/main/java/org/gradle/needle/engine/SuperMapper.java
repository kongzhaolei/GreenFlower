package org.gradle.needle.engine;

import java.util.List;

import org.gradle.needle.model.Pathdescr;
import org.gradle.needle.model.Prodata;
import org.gradle.needle.model.Propaths;
import org.gradle.needle.model.Wtinfo;
/**
 * 
 * @author kongzhaolei
 * 1. SuperMapper.xml��namespace����mapper�ӿڵ�ַ
 * 2. SuperMpper.java�ӿ��еķ�������SuperMapper.xml�е�statement��Idһ��
 * 3. SuperMpper.java�ӿ��еķ������������SuperMapper.xml�е�statement��parameterType����һ��
 * 4. SuperMpper.java�ӿ��еķ�������ֵ���ͺ�SuperMapper.xml�е�statement��resultType����һ��
 *
 */

public interface SuperMapper {
	
	public List<Prodata> selectProdata(Prodata data);
	
	public List<Propaths> selectPropaths(Propaths config);
	
	public List<Pathdescr> selectPathdescr(Pathdescr descr);
	
	public List<Wtinfo> selectWtinfo(Wtinfo wtinfo);
 
}