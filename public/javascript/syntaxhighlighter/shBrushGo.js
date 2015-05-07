/**
 * SyntaxHighlighter
 * http://alexgorbatchev.com/
 *
 * SyntaxHighlighter is donationware. If you are using it, please donate.
 * http://alexgorbatchev.com/wiki/SyntaxHighlighter:Donate
 *
 * @version
 * 3.0.83 (July 2 2010)
 *
 * @copyright
 * Copyright (C) 2004-2012 Alex Gorbatchev.
 *
 * Golang Syntax
 * Copyright (C) 2011-2012 Zuocheng Ren
 * http://www.renzuocheng.com/
 *
 * @license
 * This file is part of SyntaxHighlighter.
 *
 * SyntaxHighlighter is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SyntaxHighlighter is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SyntaxHighlighter.  If not, see <http://www.gnu.org/copyleft/lesser.html>.
 */
SyntaxHighlighter.brushes.Golang = function()
{
    var keywords =  'break default func interface select' +
        'case defer go map struct' +
        'chan else goto package switch' +
        'const fallthrough if range type' +
        'continue for import return var';

    this.regexList = [
        { regex: SyntaxHighlighter.regexLib.singleLineCComments,    css: 'comments' },      // one line comments
        { regex: SyntaxHighlighter.regexLib.multiLineCComments,     css: 'comments' },      // multiline comments
        { regex: SyntaxHighlighter.regexLib.doubleQuotedString,     css: 'string' },        // strings
        { regex: SyntaxHighlighter.regexLib.singleQuotedString,     css: 'string' },        // strings
        { regex: /\b[\d]+\.?([\d]*((e|E)(\+|\-)?[\d]+)?)?i?\b/gi,   css: 'value' },         // numbers
        { regex: /\b(0(x|X)[a-fA-F0-9]+)\b/gi,                      css: 'value' },         // hex numbers
        { regex: new RegExp(this.getKeywords(keywords), 'gm'),      css: 'keyword' }        // golang keyword
    ];

    this.forHtmlScript(SyntaxHighlighter.regexLib.aspScriptTags);
};

SyntaxHighlighter.brushes.Golang.prototype    = new SyntaxHighlighter.Highlighter();
SyntaxHighlighter.brushes.Golang.aliases      = ['golang', 'go'];
