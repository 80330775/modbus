package modbus;

import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.junit.Test;

import com.tjy.entity.Device;

public class BeanTest extends BaseTest {
	@Resource
	Map<String, Device> devices;
	
	@Test
	public void getDevices() {
		for(Entry<String, Device> entry : devices.entrySet()) {
			System.out.println(entry.getKey());
		}
	}
}
