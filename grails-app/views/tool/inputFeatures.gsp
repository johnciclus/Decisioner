<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>SustenAgro - Tool - Analysis</title>
    <asset:javascript src="jquery.validate.min.js"/>
    <asset:javascript src="localization/messages_pt_BR.min.js"/>
</head>
<body>
<div class="row main">
    <div id="content" class="col-sm-10 col-sm-offset-1 content">
        <g:if test="${inputs}">
            <g:each in="${inputs}">
                <div class="section">
                    <g:render template="/widgets/${it.widget}" model="${it.attrs}" />
                </div>
            </g:each>
        </g:if>
    </div>
</div>
<script type="text/javascript">
    $(document).ready(function() {
        backToMainTab();

        $('.pager a').click(function (e) {
            var id = $(this).attr('href');
            id = id.substring(0, id.lastIndexOf('_tab_'));
            var main_id = $('#main_tabs li.active a').attr('href');
            main_id = main_id.substring(0, main_id.lastIndexOf('_tab_'));
            if (id != main_id) {
                var parent_id = $('.nav-tabs a[href="' + $(this).attr('href') + '"]').parents('.tab-pane').attr('id');
                $('.nav-tabs a[href="' + '#' + parent_id + '"]').tab('show');
            }
            $('.nav-tabs a[href="' + $(this).attr('href') + '"]').tab('show');
            backToMainTab();
            e.preventDefault();
        });

        $(".clear").click(function(){
            var name = $(this).attr('id').replace('-clear', '');
            $("input[name='"+name+"']").removeAttr('checked');
            $($("select[name^='"+name+"'] option")[0]).prop("selected", true);
            $("textarea[name^='"+name+"']").val('');
        });

        $(".justify").click(function () {
            var name = $(this).attr('id').replace('-justify', '');
            var element = $("label[for='" + name + "-justification']").parent();
            if ($(element).hasClass("hidden"))
                $(element).addClass('show').removeClass('hidden');
            else if ($(element).hasClass("show"))
                $(element).addClass('hidden').removeClass('show');
        });

        var rules = {};

        $("input[type='radio']").each(function () {
            var e1Name = $(this).attr('name');
            var e2 = $("[name^='" + e1Name + "-weight']");
            if (e2.length) {
                var e2Name = $(e2).attr('name');
                rules[e1Name] = {
                    required: function (element) {
                        var name = $(element).attr('name');
                        return (($("[name^='" + name + "-weight']").val() != null) != $(element).is(':checked'));
                    }
                };
                rules[e2Name] = {
                    required: function (element) {
                        var name = $(element).attr('name');
                        var anotherName = name.substring(0, name.lastIndexOf('-'));
                        return (($(element).val() != null) != $("[name='" + anotherName + "']").is(':checked'));
                    }
                };
            }
        });

        var validationParams = {
            rules: rules,
            ignore: '',
            errorClass: "has-error",
            invalidHandler: function (event, validator) {
                var invalids = Object.keys(validator.invalid);
                var containers;
                var id;

                if (invalids[0]) {
                    containers = $("[name='" + invalids[0] + "']").parents("div[role='tabpanel']");
                    for (var i = containers.length; i > 0; i--) {
                        id = $(containers[i - 1]).attr('id');
                        $(".nav-tabs a[href='#" + id + "']").tab('show');
                    }
                }
            },
            errorPlacement: function (error, element) {
                var form_group = $(element).parents('.form-group');
                form_group.append(error);
            },
            highlight: function (element, errorClass, validClass) {
                //console.log('highlight');
                var form_group = $(element).parents('.form-group');
                $(element).addClass(errorClass).removeClass(validClass);
                form_group.addClass(errorClass).removeClass(validClass);
            },
            unhighlight: function (element, errorClass, validClass) {
                //console.log('unhighlight');
                var form_group = $(element).parents('.form-group');
                $(element).removeClass(errorClass).addClass(validClass);
                form_group.removeClass(errorClass).addClass(validClass);
            }
        };

        $("form").each(function (index) {
            $(this).validate(validationParams);
        });

        $('#save').click(function(){
            var button = $(this);
            var values = {};
            var valid = true;

            $("form").each(function (index) {
                valid = valid && $(this).valid();
            });

            if(valid){
                button.button('loading');
                $.each($('form').serializeArray(), function(i, field) {
                    if(field.value)
                        values[field.name] = field.value;
                });
                $.post('/tool/saveFeatures', values,
                    function (data) {
                        $('#route').parent().html(data);
                        button.button('reset');
                    }
                );
            }
        });

        function backToMainTab() {
            $('html,body').animate({
                scrollTop: 180
            }, 'fast');
        }
    });
</script>
</body>
</html>