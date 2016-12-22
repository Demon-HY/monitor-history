<form id="fm">
    <div class="form-group">
        <label class="group-label">{{t 'consoleHost_host_name'}}</label>
        <div class="group-col">
            <input class="input valid" valid="require:true,tip:'该项不能为空'" placeholder="{{t 'consoleHost_host_name'}}" type="text" name="name" />
        </div>
    </div>
    
    <div class="form-group">
        <label class="group-label">{{t 'consoleHost_host_template'}}</label>
        <div class="group-col checkbox-template"></div>
    </div>
</form>