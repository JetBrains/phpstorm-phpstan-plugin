<?php
namespace WebServCo\Framework;

final class SourceCode
{
    const TYPE_XML = 'XML';

 <warning descr="phpstan: Property WebServCo\Framework\SourceCode::$type has no typehint specified.">   protected $type;</warning>
 <warning descr="phpstan: Property WebServCo\Framework\SourceCode::$data has no typehint specified.">   protected $data;</warning>

 <warning descr="phpstan: Method WebServCo\Framework\SourceCode::__construct() has parameter $data with no typehint specified."><warning descr="phpstan: Method WebServCo\Framework\SourceCode::__construct() has parameter $type with no typehint specified.">   public function __construct($type, $data)</warning></warning>
    {
        switch ($type) {
            case self::TYPE_XML:
                break;
            default:
                throw new \WebServCo\Framework\Exceptions\ApplicationException('Type not implemented.');
 <warning descr="phpstan: Unreachable statement - code above always terminates.">               break;</warning>
        }
        $this->type = $type;
        $this->data = $data;
    }

 <warning descr="phpstan: Method WebServCo\Framework\SourceCode::highlight() has no return typehint specified.">   public function highlight()</warning>
    {
        switch ($this->type) {
            case self::TYPE_XML:
                return $this->highlightXml($this->data);
 <warning descr="phpstan: Unreachable statement - code above always terminates.">               break;</warning>
            default:
                return false;
 <warning descr="phpstan: Unreachable statement - code above always terminates.">               break;</warning>
        }
    }

 <warning descr="phpstan: Method WebServCo\Framework\SourceCode::highlightXml() has no return typehint specified."><warning descr="phpstan: Method WebServCo\Framework\SourceCode::highlightXml() has parameter $data with no typehint specified.">   protected function highlightXml($data)</warning></warning>
    {
        $data = htmlentities($data);
        $data = str_replace('&lt;', '<span style="color: purple">&lt;', $data);
        $data = str_replace('&gt;', '&gt;</span>', $data);
        return $data;
    }
}
