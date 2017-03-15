<div class="data-toolbar">
    <button class="btn btn-info add-group">{{t 'consoleGroup_add_group'}}</button>
    <button class="btn btn-info edit-group">{{t 'consoleGroup_edit_group'}}</button>
    <button class="btn btn-info delete-group">{{t 'consoleGroup_delete_group'}}</button>
    <form id="toolbar-form">
        <input type="text" name="keyword" class="toolbar-input" placeholder="{{t 'consoleGroup_group_name'}}">
    </form>
    <button class="btn btn-info" id="toolbar-search">{{t 'consoleGroup_search'}}</button>
    <button class="btn" id="toolbar-reset">{{t 'consoleGroup_reset'}}</button>
</div>

<div id="grouplist" class="grouplist"></div>