<div class="title"><label>{{t 'consoleHost_edit_host'}}</label></div>
<div class="edithost module">
    <form id="fm">
        <div class="form-line">
            <label class="line-label">{{t 'consoleHost_host_name'}}</label>
            <div class="line-con">
                <input class="input" placeholder="{{t 'consoleHost_host_name'}}" type="text" name="name" />
            </div>
        </div>
        <div class="form-line">
            <label class="line-label">{{t 'consoleHost_hostIP'}}</label>
            <div class="line-con">
                <input class="input" placeholder="{{t 'consoleHost_hostIP'}}" type="text" name="ip" />
            </div>
        </div>
        <div class="form-line">
            <label class="line-label">{{t 'consoleHost_host_group'}}</label>
            <div class="line-con mul-group"></div>
        </div>
        <div class="form-line">
            <label class="line-label">{{t 'consoleHost_host_template'}}</label>
            <div class="line-con mul-template"></div>
        </div>
        <div class="form-line">
            <label class="line-label">{{t 'consoleHost_monitored'}}</label>
            <div class="line-con">
                <select name="monitored" class="form-select">
                    <option value="agent" selected="selected">Agent</option>
                    <option value="snmp">SNMP</option>
                    <option value="wget">WGET</option>
                </select>
            </div>
        </div>
        <div class="form-line">
            <label class="line-label">{{t 'consoleHost_host_interval'}}</label>
            <div class="line-con">
                <input class="input" placeholder="{{t 'consoleHost_host_interval'}}" type="text" name="interval" />
            </div>
        </div>
        <div class="form-line">
            <label class="line-label">{{t 'consoleHost_host_status'}}</label>
            <div class="line-con">
                <select name="status" class="form-select">
                    <option value="online" selected="selected">Online</option>
                    <option value="down">Down</option>
                    <option value="unreachable">Unreachable</option>
                    <option value="offline">Offline</option>
                    <option value="problem">Problem</option>
                </select>
            </div>
        </div>
        <div class="form-line">
            <label class="line-label">{{t 'consoleHost_host_memo'}}</label>
            <div class="line-con">
                <textarea name="memo" rows="3" class="memo"></textarea>
            </div>
        </div>
    </form>
</div>
<div class="xdialog-footer">
    <button name="save" class="btn btn-primary submit-host">{{t 'confirm'}}</button>
    <button name="cancel" class="btn">{{t 'cancel'}}</button>
</div>