<form id="fm">
    <div class="form-group">
        <label class="group-label">{{t 'consoleHost_host_name'}}</label>
        <div class="group-col">
            <input class="input valid" valid="require:true,tip:'该项不能为空'" placeholder="{{t 'consoleHost_host_name'}}" type="text" name="name" />
        </div>
    </div>
    <div class="form-group">
        <label class="group-label">{{t 'consoleHost_hostIP'}}</label>
        <div class="group-col">
            <input class="input valid" valid="require:true,tip:'该项不能为空'" placeholder="{{t 'consoleHost_hostIP'}}" type="text" name="name" />
        </div>
    </div>
    <div class="form-group">
        <label class="group-label">{{t 'consoleHost_monitored'}}</label>
        <div class="group-col">
            <select name="monitored" class="form-select">
                <option value="agent" selected="selected">Agent</option>
                <option value="snmp">SNMP</option>
                <option value="wget">WGET</option>
            </select>
        </div>
    </div>
    <div class="form-group">
        <label class="group-label">{{t 'consoleHost_host_interval'}}</label>
        <div class="group-col">
            <input class="input valid" valid="require:true,tip:'该项不能为空'" placeholder="{{t 'consoleHost_host_interval'}}" type="text" name="name" />
        </div>
    </div>
    <div class="form-group">
        <label class="group-label">{{t 'consoleHost_host_status'}}</label>
        <div class="group-col">
            <select name="monitored" class="form-select">
                <option value="online" selected="selected">Online</option>
                <option value="down">Down</option>
                <option value="unreachable">Unreachable</option>
                <option value="offline">Offline</option>
                <option value="problem">Problem</option>
            </select>
        </div>
    </div>
    <div class="form-group">
        <label class="group-label">{{t 'consoleHost_memo'}}</label>
        <div class="group-col">
            <textarea name="memo" clos="20" rows="3"></textarea>
        </div>
    </div>
</form>