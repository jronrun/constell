'use strict';

(function ($, root, register, mirror) {

    var definedEx = [];
    function defineEx(cmd, exHandle, desc, shortCmd) {
        if (!cmd || !$.isFunction(exHandle)) {
            return definedEx;
        }

        var exbody = {
            cmd: cmd,
            shortCmd: shortCmd || cmd,
            exHandle: exHandle,
            desc: desc || ''
        };

        CodeMirror.Vim.defineEx(exbody.cmd, exbody.shortCmd, function(cm, params) {
            params = $.extend({
                args: [],
                argString: '',
                input: '',
                line: false,
                commandName: ''
            }, params);

            params.get = function(index, defaultValue) {
                index = index || 0;
                if (params.args && params.args.length > index) {
                    return params.args[index];
                }

                return defaultValue;
            };
            exbody.exHandle(params, cm);
        });

        definedEx.push(exbody);
    }

    var noteMirror = function (elId, options, events) {
        var aMirror = mirror(elId, $.extend({
            mode: 'markdown',
            keyMap: 'vim',
            fullScreen: true,
            autofocus: true,
            lineNumbers: true,
            lineNumberFormatter: function (line) {
                return 1 === line ? '' : line;
            },
            showCursorWhenSelecting: true,
            styleActiveLine: true,
            extraKeys: {
                "Ctrl-/": 'toggleComment',
                "Ctrl-A": 'selectAll'
            },
            cust: {
                escKey: false
            }
        }, options || {}), $.extend({
            inputRead: function(cm, changeObj) {

            }
        }, events || {})), aTarget = aMirror.target;

        var vimTools = {
            visualMode: function() {
                return CodeMirror.Vim.exitInsertMode(aTarget);
            },
            handleEx: function(input) {
                return CodeMirror.Vim.handleEx(aTarget, input);
            },
            handleKey: function(input, origin) {
                return CodeMirror.Vim.handleKey(aTarget, input, origin);
            },
            editMode: function() {
                return aMirror.vim.handleKey('i');
            },
            joinLine: function (startLine, endLine) {
                var start = startLine || 0, end = endLine || aTarget.lineCount();
                var cursor = aTarget.getCursor();

                var tmpCursor = pi.clone(cursor);
                tmpCursor.line = start;
                aTarget.setCursor(tmpCursor);
                for (var idx = start; idx < end; idx++) {
                    vimTools.handleKey('J');
                }
                aTarget.setCursor(cursor);
            }
        };

        $.extend(aMirror, {
            vim: vimTools
        });

        aMirror.autoLoadMode('markdown');

        return aMirror;
    };

    function mirrorWrap(target, events) {
        var wrapped = mirror.helpers(target, events);

        wrapped.mapkey({
            "Esc": function (cm) {
                wrapped.fullscreenTgl();
            }
        });

        return wrapped;
    }

    var helper = function (target, events) {
        var is2Panels = 2 === target.extras.panels, mergeEditors = ['left', 'middle', 'right'],
            vLeft = is2Panels ? mirrorWrap(target.editor(), events) : mirrorWrap(target.leftOriginal(), events),
            vMiddle = is2Panels ? null : mirrorWrap(target.editor(), events),
            //vRight = mirrorWrap(target.rightOriginal(), events)
            vRight = mirror.helpers(target.rightOriginal(), events);

        var tools = {
            target: target,
            left: vLeft,
            middle: vMiddle,
            right: vRight,

            tglOption: function (optionKey) {
                tools.attrs(optionKey, !tools.attrs(optionKey));
                return tools.attrs(optionKey);
            },
            attrs: function (optionKey, optionVal) {
                if (pi.isJson(optionKey)) {
                    $.each(optionKey, function (k, v) {
                        target.options[k] = v;
                    });
                    return optionKey;
                }

                if (pi.isUndefined(optionKey)) {
                    return target.options;
                }

                if (pi.isArray(optionKey)) {
                    var rAttr = {};
                    $.each(optionKey, function (k, okey) {
                        rAttr[okey] = target.options[okey];
                    });
                    return rAttr;
                }

                var aVal = target.options[optionKey];
                if (pi.isUndefined(optionVal)) {
                    return aVal;
                }

                target.options[optionKey] = optionVal;
                return aVal;
            },
            next: function () {
                CodeMirror.commands.goNextDiff(tools.left.target);
            },
            prev: function () {
                CodeMirror.commands.goPrevDiff(tools.left.target);
            },
            differencesTgl: function (manual) {
                var val = pi.isUndefined(manual) ? !tools.attrs('highlightDifferences') : manual;
                target.setShowDifferences(val);
                tools.attrs('highlightDifferences', val);
                return tools;
            },
            refresh: function (options) {
                options = options || {};
                var prevAttrs = tools.attrs(), newOptions = target.extras, th = tools.left.theme(), modeInfo = tools.left.mode(),
                    isCollapseIdentical = pi.has(prevAttrs, 'collapseIdentical') && true === prevAttrs.collapseIdentical;

                if (isCollapseIdentical) {
                    var vs = tools.viewVals();
                    if (!pi.has(options, 'orig1')) {
                        $.extend(options, { orig1: vs.left});
                    }

                    if (!pi.has(options, 'orig2')) {
                        $.extend(options, { orig2: vs.right});
                    }

                    if (!pi.has(options, 'value')) {
                        $.extend(options, { value: vs.middle});
                    }
                }

                var viewVals = tools.viewVals(options);
                $.extend(newOptions, prevAttrs, options);

                $(newOptions.elId).empty();
                var inst = mergeMirror(newOptions, events), vcm = null;
                $.each(viewVals, function (view, val) {
                    if (mergeEditors.indexOf(view) !== -1 && null != (vcm = inst[view])) {
                        vcm.val(val);
                    }
                });

                inst.actions(function (anInst) {
                    anInst.theme(th);
                    anInst.mode(modeInfo.mime, modeInfo.chosenMimeOrExt || '');
                });

                return inst;
            },

            mergedView: function () {
                return tools.is2Panels() ? tools.left : tools.middle;
            },

            is2Panels: function () {
                return 2 === target.extras.panels;
            },

            /**
             * Same action to each mirror instance
             * @param doFunc    function(inst) {}
             */
            actions: function (doFunc) {
                $.each(mergeEditors, function (idx, n) {
                    var anInst = null; if (null != (anInst = tools[n])) {
                        $.isFunction(doFunc) && doFunc(anInst);
                    }
                });
            },

            viewVals: function (options) {
                var viewVals = {}, is2Panels = tools.is2Panels();
                options = options || {};
                if (!pi.has(options, 'orig1')) {
                    viewVals.left = is2Panels ? target.editor().getValue() : target.leftOriginal().getValue();
                }

                if (!pi.has(options, 'orig2')) {
                    viewVals.right = target.rightOriginal().getValue();
                }

                if (!pi.has(options, 'value')) {
                    viewVals.middle = target.editor().getValue();
                }

                viewVals.is2Panels = is2Panels;
                return viewVals;
            },

            alignTgl: function () {
                return tools.refresh({
                    connect: tools.attrs('connect') ? null : 'align'
                });
            },
            collapseTgl: function () {
                tools.tglOption('collapseIdentical');
                var vs = tools.viewVals();
                return tools.refresh({
                    orig1: vs.left,
                    value: vs.middle,
                    orig2: vs.right
                });
            },
            allowEditOrigTgl: function () {
                tools.tglOption('allowEditingOriginals');
                return tools.refresh();
            },
            revertButtonsTgl: function () {
                tools.tglOption('revertButtons');
                return tools.refresh();
            },
            lineNumbersTgl: function () {
                tools.tglOption('lineNumbers');
                return tools.refresh();
            },
            panelsTgl: function() {
                var orig1 = '', orig2 = target.rightOriginal().getValue(),
                    value = target.editor().getValue(), pnum = null;

                switch (target.extras.panels) {
                    case 2:
                        orig1 = value;
                        pnum = 3;
                        break;
                    case 3:
                        orig1 = target.leftOriginal().getValue();
                        pnum = 2;
                        break;
                }

                return tools.refresh({
                    orig1: orig1,
                    orig2: orig2,
                    value: value,
                    panels: pnum
                });
            }
        };

        return tools;
    };

    /**
     *
     * panels 3:
     * orig1  value  orig2
     *
     * panels 2:
     * value  orig2
     * @param options
     */
    var mergeMirror = function (options, events) {
        options = $.extend({
            elId: '',
            top: 54,
            height: $(window).height() - 65,
            panels: 2,

            orig1: '',
            orig2: '',

            value: '',

            connect: null,
            mode: '',
            lineNumbers: true,
            revertButtons: true,
            showDifferences: true,
            highlightDifferences: true,
            collapseIdentical: false,
            allowEditingOriginals: true
        }, options || {});

        var aMode = null, minfo = null; if (aMode = options.mode) {
            if (!pi.isBlank(minfo = mirror.modeInfo(aMode))) {
                aMode = minfo.mode;
            }
        }

        var mv = CodeMirror.MergeView(pi.query(options.elId), {
            value: options.value,
            origLeft: options.panels === 3 ? options.orig1 : null,
            orig: options.orig2,
            lineNumbers: options.lineNumbers,
            mode: aMode,
            revertButtons: options.revertButtons,
            showDifferences: options.showDifferences,
            highlightDifferences: options.highlightDifferences,
            connect: options.connect,
            collapseIdentical: options.collapseIdentical,
            allowEditingOriginals: options.allowEditingOriginals
        });

        var me, ml, mr;
        if (me = mv.editor()) {
            me.setSize(null, options.height);
        }
        if (ml = mv.leftOriginal()) {
            ml.setSize(null, options.height);
        }
        if (mr = mv.rightOriginal()) {
            mr.setSize(null, options.height);
        }

        $(mv.wrap).css({
            top: options.top,
            border: 'none'
        }).find('.CodeMirror-merge-gap').css({
            height: options.height,
            'border-color': '#d9edf7',
            'background-color': '#ffffff'
        });
        $(mv.wrap).find('.CodeMirror-gutters').css({
            border: 'none',
            'background-color': '#ffffff'
        });

        mv.extras = {
            elId: options.elId,
            top: options.top,
            height: options.height,
            panels: options.panels
        };

        return helper(mv, events);
    };


    $.extend(noteMirror, {
        merge: mergeMirror,
        defineEx: defineEx
    });

    root[register] = noteMirror;


})(jQuery, window, 'note', mirror);