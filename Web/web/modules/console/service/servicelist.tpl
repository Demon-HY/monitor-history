<div class="data-toolbar">
    <button class="btn btn-info add-service">{{t 'consoleService_add_service'}}</button>
    <button class="btn btn-info edit-service">{{t 'consoleService_edit_service'}}</button>
    <button class="btn btn-info delete-service">{{t 'consoleService_delete_service'}}</button>
    <form id="toolbar-form">
        <input type="text" name="keyword" class="toolbar-input" placeholder="{{t 'consoleService_service_name'}}">
    </form>
    <button class="btn btn-info" id="toolbar-search">{{t 'consoleService_search'}}</button>
    <button class="btn" id="toolbar-reset">{{t 'consoleService_reset'}}</button>
</div>

<div id="servicelist" class="servicelist"></div>