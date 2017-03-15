<div class="data-toolbar">
    <button class="btn btn-info add-maintain">{{t 'consoleMaintain_add_maintain'}}</button>
    <button class="btn btn-info edit-maintain">{{t 'consoleMaintain_edit_maintain'}}</button>
    <button class="btn btn-info delete-maintain">{{t 'consoleMaintain_delete_maintain'}}</button>
    <form id="toolbar-form">
        <input type="text" name="keyword" class="toolbar-input" placeholder="{{t 'consoleMaintain_maintain_name'}}">
    </form>
    <button class="btn btn-info" id="toolbar-search">{{t 'consoleMaintain_search'}}</button>
    <button class="btn" id="toolbar-reset">{{t 'consoleMaintain_reset'}}</button>
</div>

<div id="maintainlist" class="maintainlist"></div>