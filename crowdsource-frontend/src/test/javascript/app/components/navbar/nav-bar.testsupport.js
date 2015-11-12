function NavBar(element) {

    this.getNewProjectLink = function() {
        return element.find("li.new-project a");
    };

    this.getLogoutButton = function() {
        return element.find(".logout a");
    };

}