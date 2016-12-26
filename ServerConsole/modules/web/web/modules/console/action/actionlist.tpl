<div class="data-toolbar">
    <button class="btn btn-info add-action">{{t 'consoleAction_add_action'}}</button>
    <button class="btn btn-info edit-action">{{t 'consoleAction_edit_action'}}</button>
    <button class="btn btn-info delete-action">{{t 'consoleAction_delete_action'}}</button>
    <form id="toolbar-form">
        <input type="text" name="keyword" class="toolbar-input" placeholder="{{t 'consoleAction_action_name'}}">
    </form>
    <button class="btn btn-info" id="toolbar-search">{{t 'consoleAction_search'}}</button>
    <button class="btn" id="toolbar-reset">{{t 'consoleAction_reset'}}</button>
</div>

<div id="actionlist" class="actionlist"></div>