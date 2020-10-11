import React from "react";
import { Nav, Navbar } from "react-bootstrap";
import { Link } from "react-router-dom";

function Topbar(props) {
  return (
    <Navbar collapseOnSelect expand="lg" bg="dark" variant="dark">
      <Navbar.Brand>
        <Link to={props.links[0].href}>
          <img
            src={require("../assets/wca_logo.svg")}
            width="60"
            height="60"
            className="d-inline-block align-top"
            alt="React Bootstrap logo"
          />
        </Link>
      </Navbar.Brand>
      <Navbar.Toggle aria-controls="responsive-navbar-nav" />
      <Navbar.Collapse id="responsive-navbar-nav">
        <Nav className="mr-auto">
          {props.links.map((link, i) => (
            <Link key={i} to={link.href} className="text-white">
              {link.name}
            </Link>
          ))}
        </Nav>
        <Nav>
          <Link className="text-white">Login</Link>
        </Nav>
      </Navbar.Collapse>
    </Navbar>
  );
}

export default Topbar;
