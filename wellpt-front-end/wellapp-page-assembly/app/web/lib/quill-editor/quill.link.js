import { isValidJSON } from '@framework/vue/utils/util';
import Quill from 'quill'

const Link = Quill.import('formats/link')

class CustomLink extends Link {
  static create(value) {
    if (isValidJSON(value)) {
      value = JSON.parse(value)
    }
    let node = super.create(value.href || value)
    node.setAttribute('href', value.href || value)

    if (value.target) node.setAttribute('target', value.target)
    if (value.rel) node.setAttribute('rel', value.rel)
    if (value['data-id']) node.setAttribute('data-id', value['data-id'])

    return node
  }

  static formats(domNode) {
    const href = domNode.getAttribute('href')
    let link
    if (isValidJSON(href)) {
      link = JSON.parse(href)
    }
    if (link) {
      return {
        href: link.href,
        target: link.target,
        rel: link.rel,
        'data-id': link['data-id']
      }
    }
    return {
      href: domNode.getAttribute('href'),
      target: domNode.getAttribute('target'),
      rel: domNode.getAttribute('rel'),
      'data-id': domNode.getAttribute('data-id')
    }
  }

  // static sanitize(url) {
  //   return sanitize(url, this.PROTOCOL_WHITELIST) ? url : this.SANITIZED_URL;
  // }

  format(name, value) {
    if (name !== this.statics.blotName || !value) return super.format(name, value);
    // value = this.constructor.sanitize(value);
    if (isValidJSON(value)) {
      value = JSON.parse(value)
    }
    this.domNode.setAttribute('href', value.href);
    if (value.target) this.domNode.setAttribute('target', value.target)
    if (value.rel) this.domNode.setAttribute('rel', value.rel)
    if (value['data-id']) this.domNode.setAttribute('data-id', value['data-id'])
  }
}

CustomLink.blotName = 'link'
CustomLink.tagName = 'A'

Quill.register(CustomLink, true)

export default CustomLink
