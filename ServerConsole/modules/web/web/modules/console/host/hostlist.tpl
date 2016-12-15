<div class="data-toolbar">
    <button class="btn btn-info add-host">{{t 'consoleHost_add_host'}}</button>
    <button class="btn btn-info edit-host">{{t 'consoleHost_edit_host'}}</button>
    <button class="btn btn-info delete-host">{{t 'consoleHost_delete_host'}}</button>
    <form id="toolbar-form">
        <input type="text" name="keyword" class="toolbar-input" placeholder="{{t 'consoleHost_hostIP'}}">
    </form>
    <button class="btn btn-info" id="toolbar-search">{{t 'consoleHost_search'}}</button>
    <button class="btn" id="toolbar-reset">{{t 'consoleHost_reset'}}</button>
</div>

<div id="hostlist" class="hostlist"></div>