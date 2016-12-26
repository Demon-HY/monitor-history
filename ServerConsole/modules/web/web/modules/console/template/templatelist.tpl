<div class="data-toolbar">
    <button class="btn btn-info add-template">{{t 'consoleTemplate_add_template'}}</button>
    <button class="btn btn-info edit-template">{{t 'consoleTemplate_edit_template'}}</button>
    <button class="btn btn-info delete-template">{{t 'consoleTemplate_delete_template'}}</button>
    <form id="toolbar-form">
        <input type="text" name="keyword" class="toolbar-input" placeholder="{{t 'consoleTemplate_template_name'}}">
    </form>
    <button class="btn btn-info" id="toolbar-search">{{t 'consoleTemplate_search'}}</button>
    <button class="btn" id="toolbar-reset">{{t 'consoleTemplate_reset'}}</button>
</div>

<div id="templatelist" class="templatelist"></div>