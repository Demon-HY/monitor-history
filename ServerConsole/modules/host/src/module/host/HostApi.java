package module.host;

import java.sql.SQLException;
import java.util.List;

import org.javatuples.Pair;

import module.SDK.SdkCenter;
import module.SDK.info.HostInfo;
import module.SDK.inner.IBeans;
import module.SDK.inner.IHostApi;
import monitor.exception.LogicalException;
import monitor.exception.UnInitilized;

public class HostApi implements IHostApi{
	protected IBeans beans;
	protected HostModel hostModel;
	private static HostApi hostApi;
	private HostApi(IBeans beans, HostModel hostModel) throws LogicalException {
		this.beans = beans;
		this.hostModel = hostModel;
		
		SdkCenter.getInst().addInterface(IHostApi.name, this);
	}
	
	public static void init(IBeans beans, HostModel hostModel) throws LogicalException {
		hostApi = new HostApi(beans, hostModel);
	}
	
	public static HostApi getInst() throws UnInitilized {
		if (null == hostApi) {
			throw new UnInitilized();
		}
		return hostApi;
	}

	/**
	 * 
	 * @param pageIndex 分页页码
	 * @param pageSize 分页大小
	 * @param order 排序参数（desc、asc）
	 * @param sort 排序字段
	 * @return
	 * @throws SQLException 
	 */
	public Pair<Integer, List<HostInfo>> listHost(int pageIndex, int pageSize, String order, String sort) throws SQLException {
		List<HostInfo> result = null;
		Integer count = null;
		result = this.hostModel.listGroup(pageIndex, pageSize, order, sort);
		count = this.hostModel.countHost();
		
		return new Pair<Integer, List<HostInfo>>(count, result);
	}
}
