<div class="data-toolbar">
    <button class="btn btn-info add-serviceindex">{{t 'consoleServiceIndex_add_serviceindex'}}</button>
    <button class="btn btn-info edit-serviceindex">{{t 'consoleServiceIndex_edit_serviceindex'}}</button>
    <button class="btn btn-info delete-serviceindex">{{t 'consoleServiceIndex_delete_serviceindex'}}</button>
    <form id="toolbar-form">
        <input type="text" name="keyword" class="toolbar-input" placeholder="{{t 'consoleService_service_name'}}">
    </form>
    <button class="btn btn-info" id="toolbar-search">{{t 'consoleServiceIndex_serviceindex_name'}}</button>
    <button class="btn" id="toolbar-reset">{{t 'consoleServiceIndex_reset'}}</button>
</div>

<div id="serviceindexlist" class="serviceindexlist"></div>