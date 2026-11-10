package com.ptilou.app.domain.generator

// Tire un élément au hasard dans la banque, en évitant de reproposer le même élément que
// la dernière fois (sauf si la banque n'a qu'un seul élément, auquel cas le choix est forcé).
internal fun <T> tirerSansRepeter(banque: List<T>, dernier: T?): T {
    if (banque.size <= 1) return banque.first()
    var choix: T
    do {
        choix = banque.random()
    } while (choix == dernier)
    return choix
}
