<?php
namespace WebServCo\Framework;

final class SourceCode
{
    const TYPE_XML = 'XML';

<weak_warning descr="phpstan: Property WebServCo\Framework\SourceCode::$type has no typehint specified.">   protected $type;</weak_warning>
<weak_warning descr="phpstan: Property WebServCo\Framework\SourceCode::$data has no typehint specified.">   protected $data;</weak_warning>

<weak_warning descr="phpstan: Method WebServCo\Framework\SourceCode::__construct() has parameter $data with no typehint specified."><weak_warning descr="phpstan: Method WebServCo\Framework\SourceCode::__construct() has parameter $type with no typehint specified.">   public function __construct($type, $data)</weak_warning></weak_warning>
    {
        switch ($type) {
            case self::TYPE_XML:
                break;
            default:
                throw new \WebServCo\Framework\Exceptions\ApplicationException('Type not implemented.');
<weak_warning descr="phpstan: Unreachable statement - code above always terminates.">               break;</weak_warning>
        }
        $this->type = $type;
        $this->data = $data;
    }

<weak_warning descr="phpstan: Method WebServCo\Framework\SourceCode::highlight() has no return typehint specified.">   public function highlight()</weak_warning>
    {
        switch ($this->type) {
            case self::TYPE_XML:
                return $this->highlightXml($this->data);
<weak_warning descr="phpstan: Unreachable statement - code above always terminates.">               break;</weak_warning>
            default:
                return false;
<weak_warning descr="phpstan: Unreachable statement - code above always terminates.">               break;</weak_warning>
        }
    }

<weak_warning descr="phpstan: Method WebServCo\Framework\SourceCode::highlightXml() has no return typehint specified."><weak_warning descr="phpstan: Method WebServCo\Framework\SourceCode::highlightXml() has parameter $data with no typehint specified.">   protected function highlightXml($data)</weak_warning></weak_warning>
    {
        $data = htmlentities($data);
        $data = str_replace('&lt;', '<span style="color: purple">&lt;', $data);
        $data = str_replace('&gt;', '&gt;</span>', $data);
        return $data;
    }
}
<weak_warning descr="phpstan: Call to function count() on a separate line has no effect.">\count();</weak_warning>
